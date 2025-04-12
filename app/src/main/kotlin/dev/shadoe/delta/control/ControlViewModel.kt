package dev.shadoe.delta.control

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.shadoe.delta.api.SoftApEnabledState
import dev.shadoe.delta.api.SoftApSecurityType
import dev.shadoe.delta.data.softap.SoftApControlRepository
import dev.shadoe.delta.data.softap.SoftApStateRepository
import javax.inject.Inject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.mapLatest

@HiltViewModel
class ControlViewModel
@Inject
constructor(
  private val softApControlRepository: SoftApControlRepository,
  private val state: SoftApStateRepository,
) : ViewModel() {
  companion object {
    private const val ACTION_QR_CODE_SCREEN =
      "android.settings.WIFI_DPP_CONFIGURATOR_QR_CODE_GENERATOR"
    private const val QR_CODE_EXTRA_SSID = "ssid"
    private const val QR_CODE_EXTRA_PSK = "preSharedKey"
    private const val QR_CODE_EXTRA_SECURITY = "security"
    private const val QR_CODE_EXTRA_HIDDEN = "hiddenSsid"
    private const val QR_CODE_EXTRA_HOTSPOT = "isHotspot"
  }

  private var isDppActivityAvailable = MutableStateFlow(true)

  fun startHotspot(forceRestart: Boolean = false) =
    softApControlRepository.startSoftAp(forceRestart)

  fun stopHotspot() = softApControlRepository.stopSoftAp()

  @OptIn(ExperimentalCoroutinesApi::class)
  val ssid = state.config.mapLatest { it.ssid }

  @OptIn(ExperimentalCoroutinesApi::class)
  val passphrase = state.config.mapLatest { it.passphrase }

  @OptIn(ExperimentalCoroutinesApi::class)
  val shouldShowPassphrase =
    state.config.mapLatest {
      it.securityType != SoftApSecurityType.SECURITY_TYPE_OPEN
    }

  @OptIn(ExperimentalCoroutinesApi::class)
  val enabledState = state.status.mapLatest { it.enabledState }

  @OptIn(ExperimentalCoroutinesApi::class)
  val tetheredClientCount = state.status.mapLatest { it.tetheredClients.size }

  @OptIn(ExperimentalCoroutinesApi::class)
  val shouldShowQrButton =
    combine(
      state.status.mapLatest {
        it.enabledState == SoftApEnabledState.WIFI_AP_STATE_ENABLED
      },
      isDppActivityAvailable,
    ) { p0, p1 ->
      p0 && p1
    }

  fun openQrCodeScreen(context: Context, isBigScreen: Boolean): Boolean {
    try {
      Intent(ACTION_QR_CODE_SCREEN)
        .apply {
          state.config.value.let {
            putExtra(QR_CODE_EXTRA_SSID, it.ssid)
            putExtra(QR_CODE_EXTRA_SECURITY, it.securityType)
            if (it.securityType != SoftApSecurityType.SECURITY_TYPE_OPEN) {
              putExtra(QR_CODE_EXTRA_PSK, it.passphrase)
            }
            putExtra(QR_CODE_EXTRA_HIDDEN, it.isHidden)
            putExtra(QR_CODE_EXTRA_HOTSPOT, true)
          }
          if (isBigScreen) {
            addFlags(
              Intent.FLAG_ACTIVITY_LAUNCH_ADJACENT or
                Intent.FLAG_ACTIVITY_NEW_TASK
            )
          }
        }
        .let { context.startActivity(it) }
    } catch (_: ActivityNotFoundException) {
      isDppActivityAvailable.value = false
    }
    return isDppActivityAvailable.value
  }
}
