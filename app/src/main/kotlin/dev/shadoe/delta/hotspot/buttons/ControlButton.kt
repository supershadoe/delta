package dev.shadoe.delta.hotspot.buttons

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.min
import dev.shadoe.delta.R

@Composable
internal fun ControlButton(
  shape: Shape,
  interactionSource: MutableInteractionSource,
  isEnabled: Boolean,
  isLoading: Boolean,
  onClick: () -> Unit,
  content: @Composable BoxScope.() -> Unit,
) {
  val color =
    if (isLoading) {
      MaterialTheme.colorScheme.secondaryContainer
    } else if (isEnabled) {
      MaterialTheme.colorScheme.primaryContainer
    } else {
      MaterialTheme.colorScheme.surfaceBright
    }
  val description =
    if (isLoading) {
      stringResource(R.string.control_button_loading)
    } else if (isEnabled) {
      stringResource(R.string.control_button_stop)
    } else {
      stringResource(R.string.control_button_start)
    }
  // TODO: remove this comment
  //    val btnSize = min(
  //        LocalConfiguration.current.screenWidthDp.dp,
  //        LocalConfiguration.current.screenHeightDp.dp
  //    ) / 2
  //    println("btnSize: $btnSize")
  Box(
    modifier =
      Modifier.size(
          min(
            LocalConfiguration.current.screenWidthDp.dp,
            LocalConfiguration.current.screenHeightDp.dp,
          ) / 2
        )
        .padding(16.dp)
        .clip(shape = shape)
        .background(color)
        .clickable(
          interactionSource = interactionSource,
          indication = null,
          onClick = onClick,
        )
        .semantics(
          mergeDescendants = true,
          properties = { contentDescription = description },
        ),
    contentAlignment = Alignment.Center,
    content = content,
  )
}
