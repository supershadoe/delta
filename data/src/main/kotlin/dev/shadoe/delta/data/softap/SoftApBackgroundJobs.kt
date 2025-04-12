package dev.shadoe.delta.data.softap

import android.net.wifi.IStringListener
import android.net.wifi.IWifiManager
import android.net.wifi.SoftApConfigurationHidden
import android.os.Build
import dev.rikka.tools.refine.Refine
import dev.shadoe.delta.data.qualifiers.WifiSystemService
import dev.shadoe.delta.data.softap.internal.Extensions.toBridgeClass
import dev.shadoe.delta.data.softap.internal.Utils.generateRandomPassword
import javax.inject.Inject
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
  }

  override fun close() {
    scope.cancel("SoftApBackgroundJobs stopped gracefully.")
  }
}
