package dev.shadoe.delta.hotspot.buttons

import androidx.compose.runtime.Composable
import dev.shadoe.hotspotapi.WifiApEnabledStates

@Composable
internal fun HotspotButton(
    enabledState: Int,
    startHotspot: () -> Unit,
    stopHotspot: () -> Unit
) {
    when (enabledState) {
        WifiApEnabledStates.WIFI_AP_STATE_DISABLED -> LoadedButton(
            isEnabled = false
        ) {
            startHotspot()
        }

        WifiApEnabledStates.WIFI_AP_STATE_ENABLING -> LoadingButton()
        WifiApEnabledStates.WIFI_AP_STATE_ENABLED -> LoadedButton(
            isEnabled = true
        ) {
            stopHotspot()
        }

        WifiApEnabledStates.WIFI_AP_STATE_DISABLING -> LoadingButton()
        WifiApEnabledStates.WIFI_AP_STATE_FAILED -> FailedButton()
    }
}
