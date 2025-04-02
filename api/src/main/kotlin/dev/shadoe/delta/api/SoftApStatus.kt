package dev.shadoe.delta.api

data class SoftApStatus(
  @SoftApEnabledState.EnabledStateType val enabledState: Int,
  val isDppSupported: Boolean,
  val tetheredClients: List<TetheredClient>,
  val capabilities: SoftApCapabilities,
)
