package dev.shadoe.delta.data.softap

import dev.shadoe.delta.api.SoftApAutoShutdownTimeout
import dev.shadoe.delta.api.SoftApCapabilities
import dev.shadoe.delta.api.SoftApConfiguration
import dev.shadoe.delta.api.SoftApEnabledState
import dev.shadoe.delta.api.SoftApRandomizationSetting
import dev.shadoe.delta.api.SoftApSecurityType
import dev.shadoe.delta.api.SoftApSpeedType
import dev.shadoe.delta.api.SoftApStatus
import dev.shadoe.delta.data.softap.internal.InternalState
import dev.shadoe.delta.data.softap.internal.Utils.generateRandomPassword
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

@Singleton
class SoftApStateRepository @Inject constructor() {
  internal val internalState =
    MutableStateFlow(
      InternalState(fallbackPassphrase = generateRandomPassword())
    )

  internal val mStatus =
    MutableStateFlow(
      SoftApStatus(
        enabledState = SoftApEnabledState.WIFI_AP_STATE_DISABLED,
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

  internal val mConfig =
    MutableStateFlow(
      SoftApConfiguration(
        ssid = "not configured",
        passphrase = "none",
        securityType = SoftApSecurityType.SECURITY_TYPE_OPEN,
        macRandomizationSetting = SoftApRandomizationSetting.RANDOMIZATION_NONE,
        isHidden = false,
        speedType = SoftApSpeedType.BAND_2GHZ,
        blockedDevices = listOf(),
        allowedClients = listOf(),
        isAutoShutdownEnabled = false,
        autoShutdownTimeout = SoftApAutoShutdownTimeout.DEFAULT,
        maxClientLimit = 0,
      )
    )

  val config = mConfig.asStateFlow()

  val status = mStatus.asStateFlow()
}
