package dev.shadoe.delta.hotspot.buttons

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.WifiTethering
import androidx.compose.material.icons.rounded.WifiTetheringOff
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.graphics.shapes.CornerRounding
import androidx.graphics.shapes.Morph
import androidx.graphics.shapes.RoundedPolygon
import androidx.graphics.shapes.circle
import androidx.graphics.shapes.pillStar
import androidx.graphics.shapes.star
import dev.shadoe.delta.shapes.MorphingShape

@Composable
internal fun LoadedButton(isEnabled: Boolean, onClick: () -> Unit) {
  val roundedPillStar = remember {
    RoundedPolygon.pillStar(
      width = 1f,
      height = 1f,
      innerRadiusRatio = 0.9f,
      numVerticesPerRadius = 12,
      rounding = CornerRounding(0.1f),
    )
  }
  val circle = remember { RoundedPolygon.circle(numVertices = 6) }
  val roundedStar = remember {
    RoundedPolygon.star(
      numVerticesPerRadius = 6,
      rounding = CornerRounding(0.1f),
    )
  }

  val morphPillStarToCircle = remember {
    Morph(start = roundedPillStar, end = circle)
  }
  val morphCircleToStar = remember { Morph(start = circle, end = roundedStar) }

  val startAnimState =
    animateFloatAsState(targetValue = 1f, label = "HotspotButtonLoadedAnim")

  val interactionSource = remember { MutableInteractionSource() }
  val isPressed by interactionSource.collectIsPressedAsState()::value
  val loadAnimFlag = remember { mutableStateOf(false) }
  val loadAnimState =
    animateFloatAsState(
      targetValue = if (isPressed || loadAnimFlag.value) 1f else 0f,
      animationSpec =
        spring(
          dampingRatio = Spring.DampingRatioMediumBouncy,
          stiffness = Spring.StiffnessHigh,
        ),
      label = "HotspotButtonClickedAnim",
    )

  val shape =
    if (startAnimState.value == 1f) {
      MorphingShape(morph = morphCircleToStar, percentage = loadAnimState.value)
    } else {
      MorphingShape(
        morph = morphPillStarToCircle,
        percentage = startAnimState.value,
      )
    }

  ControlButton(
    shape = shape,
    interactionSource = interactionSource,
    isEnabled = isEnabled,
    isLoading = false,
    onClick = {
      loadAnimFlag.value = true
      onClick()
    },
  ) {
    Icon(
      imageVector =
        if (isEnabled) {
          Icons.Rounded.WifiTethering
        } else {
          Icons.Rounded.WifiTetheringOff
        },
      contentDescription = "Tethering",
      modifier = Modifier.size(64.dp),
    )
  }
}
