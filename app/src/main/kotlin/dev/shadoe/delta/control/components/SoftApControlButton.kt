package dev.shadoe.delta.control.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Stop
import androidx.compose.material.icons.rounded.Wifi
import androidx.compose.material.icons.rounded.Wifi1Bar
import androidx.compose.material.icons.rounded.Wifi2Bar
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
private fun LoadingIcon(
  modifier: Modifier = Modifier,
  isDisabling: Boolean = false,
) {
  val infiniteTransition = rememberInfiniteTransition()
  val loadingState =
    infiniteTransition.animateFloat(
      initialValue = if (isDisabling) 0.66f else 0f,
      targetValue = if (isDisabling) 0f else 1f,
      animationSpec =
        infiniteRepeatable(tween(durationMillis = 2000, easing = LinearEasing)),
    )
  Icon(
    imageVector =
      when (loadingState.value) {
        in 0f..0.33f -> Icons.Rounded.Wifi1Bar
        in 0.33f..0.66f -> Icons.Rounded.Wifi2Bar
        else -> Icons.Rounded.Wifi
      },
    contentDescription = stringResource(R.string.control_button_loading),
    modifier = modifier,
  )
}

@Composable
fun SoftApControlButton(
  enabledState: Int,
  startHotspot: () -> Unit,
  stopHotspot: () -> Unit,
) {
  val modifier = Modifier.size(48.dp)
  IconButton(
    onClick = {
      when (enabledState) {
        SoftApEnabledState.WIFI_AP_STATE_DISABLED -> startHotspot()
        SoftApEnabledState.WIFI_AP_STATE_ENABLED -> stopHotspot()
        else -> {}
      }
    }
  ) {
    when (enabledState) {
      SoftApEnabledState.WIFI_AP_STATE_DISABLING ->
        LoadingIcon(modifier, isDisabling = true)
      SoftApEnabledState.WIFI_AP_STATE_ENABLING ->
        LoadingIcon(modifier, isDisabling = false)
      SoftApEnabledState.WIFI_AP_STATE_DISABLED ->
        Icon(
          imageVector = Icons.Rounded.PlayArrow,
          contentDescription = stringResource(R.string.control_button_start),
          modifier,
        )
      SoftApEnabledState.WIFI_AP_STATE_ENABLED ->
        Icon(
          imageVector = Icons.Rounded.Stop,
          contentDescription = stringResource(R.string.control_button_stop),
          modifier,
        )
      SoftApEnabledState.WIFI_AP_STATE_FAILED ->
        Icon(
          imageVector = Icons.Rounded.WifiTetheringError,
          contentDescription = stringResource(R.string.failed_button),
          modifier,
        )
      else -> {}
    }
  }
}
