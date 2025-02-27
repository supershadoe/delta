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
    hotspotApi.config.value = config
    val enabled = SoftApEnabledState.WIFI_AP_STATE_ENABLED
    val disabled = SoftApEnabledState.WIFI_AP_STATE_DISABLED
    val failed = SoftApEnabledState.WIFI_AP_STATE_FAILED
    if (hotspotApi.status.value.enabledState == failed) {
        hotspotApi.startHotspot(forceRestart = true)
        while (hotspotApi.status.value.enabledState != enabled) {
            delay(500.milliseconds)
        }
    } else if (hotspotApi.status.value.enabledState == enabled) {
        hotspotApi.stopHotspot()
        while (hotspotApi.status.value.enabledState != disabled) {
            delay(500.milliseconds)
        }
        hotspotApi.startHotspot()
        while (hotspotApi.status.value.enabledState != enabled) {
            delay(500.milliseconds)
        }
    }
}
