package dev.shadoe.delta.common

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Dashboard
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.ui.graphics.vector.ImageVector
import dev.shadoe.delta.R

enum class SoftApScreenDestinations(
  @StringRes val label: Int,
  val icon: ImageVector,
  @StringRes val contentDescription: Int,
) {
  CONTROL(
    label = R.string.controls_icon,
    Icons.Rounded.Dashboard,
    contentDescription = R.string.controls_icon,
  ),
  SETTINGS(
    label = R.string.settings,
    Icons.Rounded.Settings,
    contentDescription = R.string.settings,
  ),
}
