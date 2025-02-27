package dev.shadoe.hotspotapi

import dev.shadoe.hotspotapi.helper.SoftApEnabledState.EnabledStateType
import dev.shadoe.hotspotapi.helper.SoftApSpeedType.BandType
import dev.shadoe.hotspotapi.helper.TetheredClientWrapper

data class SoftApStatus(
    @EnabledStateType val enabledState: Int,
    val tetheredClients: List<TetheredClientWrapper>,
    @BandType val supportedSpeedTypes: List<Int>,
    val maxSupportedClients: Int,
)
