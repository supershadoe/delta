package dev.shadoe.delta.control

import android.content.Context
import android.content.Intent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.shadoe.delta.api.SoftApEnabledState
import dev.shadoe.delta.api.SoftApSecurityType
import dev.shadoe.delta.data.softap.SoftApRepository
import javax.inject.Inject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.mapLatest

@HiltViewModel
class ControlViewModel
@Inject
constructor(private val softApRepository: SoftApRepository) : ViewModel() {
  companion object {
    private const val ACTION_QR_CODE_SCREEN =
      "android.settings.WIFI_DPP_CONFIGURATOR_QR_CODE_GENERATOR"
    private const val QR_CODE_EXTRA_SSID = "ssid"
    private const val QR_CODE_EXTRA_PSK = "preSharedKey"
    private const val QR_CODE_EXTRA_SECURITY = "security"
    private const val QR_CODE_EXTRA_HIDDEN = "hiddenSsid"
    private const val QR_CODE_EXTRA_HOTSPOT = "isHotspot"
  }

  private val softApClosable =
    softApRepository.callbackSubscriber(viewModelScope)

  override fun onCleared() {
    runCatching { softApClosable.close() }
    super.onCleared()
  }

  fun startHotspot(forceRestart: Boolean = false) =
    softApRepository.startHotspot(forceRestart)

  fun stopHotspot() = softApRepository.stopHotspot()

  @OptIn(ExperimentalCoroutinesApi::class)
  val ssid
    get() = softApRepository.config.mapLatest { it.ssid }

  @OptIn(ExperimentalCoroutinesApi::class)
  val passphrase
    get() = softApRepository.config.mapLatest { it.passphrase }

  @OptIn(ExperimentalCoroutinesApi::class)
  val shouldShowPassphrase =
    softApRepository.config.mapLatest {
      it.securityType != SoftApSecurityType.SECURITY_TYPE_OPEN
    }

  @OptIn(ExperimentalCoroutinesApi::class)
  val enabledState
    get() = softApRepository.status.mapLatest { it.enabledState }

  @OptIn(ExperimentalCoroutinesApi::class)
  val tetheredClientCount
    get() = softApRepository.status.mapLatest { it.tetheredClients.size }

  @OptIn(ExperimentalCoroutinesApi::class)
  val shouldShowQrButton
    get() =
      softApRepository.status.mapLatest {
        it.enabledState == SoftApEnabledState.WIFI_AP_STATE_ENABLED &&
          it.isDppSupported
      }

  fun openQrCodeScreen(context: Context) {
    Intent(ACTION_QR_CODE_SCREEN)
      .apply {
        val config = softApRepository.config.value
        putExtra(QR_CODE_EXTRA_SSID, config.ssid)
        putExtra(QR_CODE_EXTRA_SECURITY, config.securityType)
        if (config.securityType != SoftApSecurityType.SECURITY_TYPE_OPEN) {
          putExtra(QR_CODE_EXTRA_PSK, config.passphrase)
        }
        putExtra(QR_CODE_EXTRA_HIDDEN, config.isHidden)
        putExtra(QR_CODE_EXTRA_HOTSPOT, true)
      }
      .let { context.startActivity(it) }
  }
}
