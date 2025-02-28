package dev.shadoe.delta.api

data class SoftApStatus(
  @SoftApEnabledState.EnabledStateType val enabledState: Int,
  val tetheredClients: List<TetheredClient>,
  @SoftApSpeedType.BandType val supportedSpeedTypes: List<Int>,
  val maxSupportedClients: Int,
)
