package dev.shadoe.hotspotapi

import dev.shadoe.hotspotapi.wrappers.SoftApEnabledState.EnabledStateType
import dev.shadoe.hotspotapi.wrappers.SoftApSpeedType.BandType
import dev.shadoe.hotspotapi.wrappers.TetheredClientWrapper

data class SoftApStatus(
    @EnabledStateType val enabledState: Int,
    val tetheredClients: List<TetheredClientWrapper>,
    @BandType val supportedSpeedTypes: List<Int>,
    val maxSupportedClients: Int,
)
