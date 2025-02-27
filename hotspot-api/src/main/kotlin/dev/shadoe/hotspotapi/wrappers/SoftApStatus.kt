package dev.shadoe.hotspotapi.wrappers

data class SoftApStatus(
    @SoftApEnabledState.EnabledStateType val enabledState: Int,
    val tetheredClients: List<TetheredClientWrapper>,
    @SoftApSpeedType.BandType val supportedSpeedTypes: List<Int>,
    val maxSupportedClients: Int,
)
