package dev.shadoe.delta.data.softap

import android.net.wifi.IWifiManager
import android.net.wifi.SoftApConfigurationHidden
import dev.rikka.tools.refine.Refine
import dev.shadoe.delta.api.SoftApCapabilities
import dev.shadoe.delta.api.SoftApStatus
import dev.shadoe.delta.data.qualifiers.WifiSystemService
import dev.shadoe.delta.data.softap.internal.Extensions.toBridgeClass
import dev.shadoe.delta.data.softap.internal.InternalState
import dev.shadoe.delta.data.softap.internal.Utils.generateRandomPassword
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.MutableStateFlow

@Singleton
class SoftApStateRepository
@Inject
constructor(@WifiSystemService private val wifiManager: IWifiManager) {
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
}
