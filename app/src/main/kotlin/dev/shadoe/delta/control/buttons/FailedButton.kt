package dev.shadoe.delta.control.buttons

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.WifiTetheringError
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.graphics.shapes.RoundedPolygon
import androidx.graphics.shapes.circle
import dev.shadoe.delta.R
import dev.shadoe.delta.common.shapes.PolygonShape

@Composable
internal fun FailedButton(forceRestart: () -> Unit) {
  ControlButton(
    shape = PolygonShape(RoundedPolygon.circle(numVertices = 6)),
    interactionSource = remember { MutableInteractionSource() },
    isEnabled = false,
    isLoading = true,
    onClick = forceRestart,
  ) {
    Icon(
      imageVector = Icons.Rounded.WifiTetheringError,
      contentDescription = stringResource(R.string.failed_button),
      modifier = Modifier.size(64.dp),
    )
  }
}
