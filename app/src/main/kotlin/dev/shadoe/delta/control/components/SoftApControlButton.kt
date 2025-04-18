package dev.shadoe.delta.control.components

import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.HourglassTop
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Stop
import androidx.compose.material.icons.rounded.WifiTetheringError
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import dev.shadoe.delta.R
import dev.shadoe.delta.api.SoftApEnabledState

@Composable
fun SoftApControlButton(
  enabledState: Int,
  startHotspot: () -> Unit,
  stopHotspot: () -> Unit,
) {
  IconButton(
    onClick = {
      when (enabledState) {
        SoftApEnabledState.WIFI_AP_STATE_DISABLED -> startHotspot()
        SoftApEnabledState.WIFI_AP_STATE_ENABLED -> stopHotspot()
        else -> {}
      }
    }
  ) {
    Icon(
      imageVector =
        when (enabledState) {
          SoftApEnabledState.WIFI_AP_STATE_DISABLING,
          SoftApEnabledState.WIFI_AP_STATE_ENABLING ->
            Icons.Rounded.HourglassTop
          SoftApEnabledState.WIFI_AP_STATE_DISABLED -> Icons.Rounded.PlayArrow
          SoftApEnabledState.WIFI_AP_STATE_ENABLED -> Icons.Rounded.Stop
          SoftApEnabledState.WIFI_AP_STATE_FAILED ->
            Icons.Rounded.WifiTetheringError
          else -> Icons.Rounded.WifiTetheringError
        },
      contentDescription =
        when (enabledState) {
          SoftApEnabledState.WIFI_AP_STATE_DISABLING,
          SoftApEnabledState.WIFI_AP_STATE_ENABLING ->
            stringResource(R.string.control_button_loading)
          SoftApEnabledState.WIFI_AP_STATE_DISABLED ->
            stringResource(R.string.control_button_start)
          SoftApEnabledState.WIFI_AP_STATE_ENABLED ->
            stringResource(R.string.control_button_stop)
          SoftApEnabledState.WIFI_AP_STATE_FAILED ->
            stringResource(R.string.failed_button)
          else -> ""
        },
      modifier = Modifier.size(48.dp),
    )
  }
}
