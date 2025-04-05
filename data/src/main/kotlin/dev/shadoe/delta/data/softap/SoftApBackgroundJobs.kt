package dev.shadoe.delta.data.softap

import android.net.wifi.IStringListener
import android.net.wifi.IWifiManager
import android.net.wifi.SoftApConfigurationHidden
import android.os.Build
import dev.rikka.tools.refine.Refine
import dev.shadoe.delta.api.SoftApEnabledState
import dev.shadoe.delta.data.qualifiers.WifiSystemService
import dev.shadoe.delta.data.softap.internal.Extensions.toBridgeClass
import dev.shadoe.delta.data.softap.internal.Utils.generateRandomPassword
import javax.inject.Inject
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update

class SoftApBackgroundJobs
@Inject
constructor(
  @WifiSystemService private val wifiManager: IWifiManager,
  private val softApControlRepository: SoftApControlRepository,
  private val softApStateRepository: SoftApStateRepository,
) : AutoCloseable {
  private val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())

  private val updateConfigOnExternalChange =
    flow {
        var prev =
          Refine.unsafeCast<SoftApConfigurationHidden>(
            wifiManager.softApConfiguration
          )
        emit(prev)
        while (true) {
          runCatching {
              Refine.unsafeCast<SoftApConfigurationHidden>(
                wifiManager.softApConfiguration
              )
            }
            .getOrNull()
            ?.let { curr ->
              if (prev != curr) {
                emit(curr)
                prev = curr
              }
              delay(1.seconds)
            }
        }
      }
      .onEach {
        softApStateRepository.mConfig.value =
          it.toBridgeClass(state = softApStateRepository.internalState.value)
      }

  private val restartOnConfigChange =
    softApStateRepository.internalState.onEach {
      if (!it.shouldRestart) return@onEach

      val enabled = SoftApEnabledState.WIFI_AP_STATE_ENABLED
      val disabled = SoftApEnabledState.WIFI_AP_STATE_DISABLED
      val status = softApStateRepository.mStatus
      if (status.value.enabledState == enabled) {
        softApControlRepository.stopSoftAp()
        while (status.value.enabledState != disabled) {
          delay(500.milliseconds)
        }
        softApControlRepository.startSoftAp()
        while (status.value.enabledState != enabled) {
          delay(500.milliseconds)
        }
      }

      softApStateRepository.internalState.update {
        it.copy(shouldRestart = false)
      }
    }

  init {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
      wifiManager.queryLastConfiguredTetheredApPassphraseSinceBoot(
        object : IStringListener.Stub() {
          override fun onResult(value: String?) {
            softApStateRepository.internalState.update {
              it.copy(fallbackPassphrase = value ?: generateRandomPassword())
            }
            softApStateRepository.mConfig.update {
              it.copy(
                passphrase =
                  softApStateRepository.internalState.value.fallbackPassphrase
              )
            }
          }
        }
      )
    }
    updateConfigOnExternalChange.launchIn(scope)
    restartOnConfigChange.launchIn(scope)
  }

  override fun close() {
    scope.cancel("SoftApBackgroundJobs stopped gracefully.")
  }
}
