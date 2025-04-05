package dev.shadoe.delta.control

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.shadoe.delta.api.SoftApEnabledState
import dev.shadoe.delta.api.SoftApSecurityType
import dev.shadoe.delta.data.softap.SoftApBackgroundJobs
import dev.shadoe.delta.data.softap.SoftApControlRepository
import dev.shadoe.delta.data.softap.SoftApStateListener
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
  private val softApBackgroundJobs: SoftApBackgroundJobs,
  private val softApControlRepository: SoftApControlRepository,
  private val softApStateListener: SoftApStateListener,
  private val softApStateRepository: SoftApStateRepository,
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

  override fun onCleared() {
    runCatching { softApBackgroundJobs.close() }
    runCatching { softApStateListener.close() }
    super.onCleared()
  }

  fun startHotspot(forceRestart: Boolean = false) =
    softApControlRepository.startSoftAp(forceRestart)

  fun stopHotspot() = softApControlRepository.stopSoftAp()

  @OptIn(ExperimentalCoroutinesApi::class)
  val ssid = softApStateRepository.config.mapLatest { it.ssid }

  @OptIn(ExperimentalCoroutinesApi::class)
  val passphrase = softApStateRepository.config.mapLatest { it.passphrase }

  @OptIn(ExperimentalCoroutinesApi::class)
  val shouldShowPassphrase =
    softApStateRepository.config.mapLatest {
      it.securityType != SoftApSecurityType.SECURITY_TYPE_OPEN
    }

  @OptIn(ExperimentalCoroutinesApi::class)
  val enabledState = softApStateRepository.status.mapLatest { it.enabledState }

  @OptIn(ExperimentalCoroutinesApi::class)
  val tetheredClientCount =
    softApStateRepository.status.mapLatest { it.tetheredClients.size }

  @OptIn(ExperimentalCoroutinesApi::class)
  val shouldShowQrButton =
    combine(
      softApStateRepository.status.mapLatest {
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
          softApStateRepository.config.value.let {
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
