package dev.shadoe.delta.data.softap

import android.net.wifi.IWifiManager
import android.net.wifi.SoftApConfigurationHidden
import android.os.Build
import android.util.Log
import dev.rikka.tools.refine.Refine
import dev.shadoe.delta.api.SoftApCapabilities
import dev.shadoe.delta.api.SoftApConfiguration
import dev.shadoe.delta.api.SoftApStatus
import dev.shadoe.delta.data.qualifiers.WifiSystemService
import dev.shadoe.delta.data.softap.internal.Extensions.toBridgeClass
import dev.shadoe.delta.data.softap.internal.Extensions.toOriginalClass
import dev.shadoe.delta.data.softap.internal.InternalState
import dev.shadoe.delta.data.softap.internal.Utils.ADB_PACKAGE_NAME
import dev.shadoe.delta.data.softap.internal.Utils.generateRandomPassword
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

@Singleton
class SoftApStateRepository
@Inject
constructor(@WifiSystemService private val wifiManager: IWifiManager) {
  companion object {
    private const val TAG = "SoftApStateRepository"
  }

  internal val internalState =
    MutableStateFlow(
      InternalState(fallbackPassphrase = generateRandomPassword())
    )

  internal val shouldRestart = MutableStateFlow(false)

  internal val status =
    MutableStateFlow(
      SoftApStatus(
        enabledState = wifiManager.wifiApEnabledState,
        tetheredClients = emptyList(),
        capabilities =
          SoftApCapabilities(
            maxSupportedClients = 0,
            clientForceDisconnectSupported = false,
            isMacAddressCustomizationSupported = false,
            supportedFrequencyBands = emptyList(),
            supportedSecurityTypes = emptyList(),
          ),
      )
    )

  internal val config =
    MutableStateFlow(
      Refine.unsafeCast<SoftApConfigurationHidden>(
          wifiManager.softApConfiguration
        )
        .toBridgeClass(state = internalState.value)
    )

  fun updateSoftApConfiguration(c: SoftApConfiguration): Boolean =
    runCatching {
        Refine.unsafeCast<android.net.wifi.SoftApConfiguration>(
            c.toOriginalClass()
          )
          .let {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
              if (!wifiManager.validateSoftApConfiguration(it)) {
                return@let false
              }
            }
            wifiManager.setSoftApConfiguration(it, ADB_PACKAGE_NAME)
            return@let true
          }
      }
      .onFailure { Log.e(TAG, it.stackTraceToString()) }
      .getOrDefault(false)
      .also {
        if (it) {
          config.update { c }
          shouldRestart.value = true
        }
      }
}
