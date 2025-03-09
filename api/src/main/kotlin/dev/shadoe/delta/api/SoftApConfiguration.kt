package dev.shadoe.delta.api

data class SoftApConfiguration(
  val ssid: String?,
  val passphrase: String,
  @SoftApSecurityType.SecurityType val securityType: Int,
  @SoftApRandomizationSetting.RandomizationType
  val macRandomizationSetting: Int,
  val isHidden: Boolean,
  @SoftApSpeedType.BandType val speedType: Int,
  val blockedDevices: List<ACLDevice>,
  val allowedClients: List<ACLDevice>,
  val isAutoShutdownEnabled: Boolean,
  /** Timeout in milliseconds for auto shutdown. */
  val autoShutdownTimeout: Long,
  val maxClientLimit: Int,
)
