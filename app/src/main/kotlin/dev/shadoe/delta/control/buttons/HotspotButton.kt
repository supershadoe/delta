package dev.shadoe.delta.control.buttons

import androidx.compose.runtime.Composable
import dev.shadoe.delta.api.SoftApEnabledState

@Composable
internal fun HotspotButton(
  enabledState: Int,
  startHotspot: () -> Unit,
  stopHotspot: () -> Unit,
) {
  when (enabledState) {
    SoftApEnabledState.WIFI_AP_STATE_DISABLED ->
      LoadedButton(isEnabled = false, onClick = startHotspot)

    SoftApEnabledState.WIFI_AP_STATE_ENABLING -> LoadingButton()
    SoftApEnabledState.WIFI_AP_STATE_ENABLED ->
      LoadedButton(isEnabled = true, onClick = stopHotspot)

    SoftApEnabledState.WIFI_AP_STATE_DISABLING -> LoadingButton()
    SoftApEnabledState.WIFI_AP_STATE_FAILED -> FailedButton()
  }
}
