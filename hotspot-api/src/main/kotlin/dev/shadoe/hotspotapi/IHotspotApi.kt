package dev.shadoe.hotspotapi

import dev.shadoe.hotspotapi.wrappers.SoftApConfiguration
import dev.shadoe.hotspotapi.wrappers.SoftApStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

interface IHotspotApi {
    val config: MutableStateFlow<SoftApConfiguration>

    val status: StateFlow<SoftApStatus>

    fun startHotspot(forceRestart: Boolean = false)

    fun stopHotspot()
}
