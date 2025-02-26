package dev.shadoe.delta.hotspot

import dev.shadoe.hotspotapi.HotspotApi
import dev.shadoe.hotspotapi.SoftApConfiguration
import dev.shadoe.hotspotapi.helper.SoftApEnabledState
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.milliseconds

internal suspend fun setSoftApConfiguration(
    hotspotApi: HotspotApi,
    config: SoftApConfiguration,
) {
    hotspotApi.setSoftApConfiguration(config)
    val enabled = SoftApEnabledState.WIFI_AP_STATE_ENABLED
    val disabled = SoftApEnabledState.WIFI_AP_STATE_DISABLED
    val failed = SoftApEnabledState.WIFI_AP_STATE_FAILED
    if (hotspotApi.enabledState.value == failed) {
        hotspotApi.startHotspot(forceRestart = true)
        while (hotspotApi.enabledState.value != enabled) {
            delay(500.milliseconds)
        }
    } else if (hotspotApi.enabledState.value == enabled) {
        hotspotApi.stopHotspot()
        while (hotspotApi.enabledState.value != disabled) {
            delay(500.milliseconds)
        }
        hotspotApi.startHotspot()
        while (hotspotApi.enabledState.value != enabled) {
            delay(500.milliseconds)
        }
    }
}
