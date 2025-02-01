package dev.shadoe.delta.hotspot.buttons

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.WifiTethering
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.graphics.shapes.CornerRounding
import androidx.graphics.shapes.Morph
import androidx.graphics.shapes.RoundedPolygon
import androidx.graphics.shapes.pillStar
import androidx.graphics.shapes.star
import dev.shadoe.delta.shapes.MorphingShape
import dev.shadoe.delta.shapes.RotatingShape

@Composable
internal fun LoadingButton() {
    val roundedStar = remember {
        RoundedPolygon.star(
            numVerticesPerRadius = 6, rounding = CornerRounding(0.1f)
        )
    }
    val roundedPillStar = remember {
        RoundedPolygon.pillStar(
            width = 1f,
            height = 1f,
            innerRadiusRatio = 0.9f,
            numVerticesPerRadius = 12,
            rounding = CornerRounding(0.1f)
        )
    }
    val morphStarToPillStar = remember {
        Morph(start = roundedStar, end = roundedPillStar)
    }

    val startAnimState = animateFloatAsState(
        targetValue = 1f,
        label = "HotspotButtonLoadingAnim",
    )

    val infiniteTransition =
        rememberInfiniteTransition("HotspotLoadingInfTransition")
    val rotationState = infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            tween(6000, easing = LinearEasing),
        ),
        label = "HotspotLoadingRotationAnim"
    )

    val shape = if (startAnimState.value == 1f) {
        RotatingShape(
            polygon = roundedPillStar, degrees = rotationState.value
        )
    } else {
        MorphingShape(
            morph = morphStarToPillStar, percentage = startAnimState.value
        )
    }

    ControlButton(
        shape = shape,
        interactionSource = remember { MutableInteractionSource() },
        isEnabled = false,
        isLoading = true,
        onClick = {},
    ) {
        Icon(
            imageVector = Icons.Rounded.WifiTethering,
            contentDescription = "Tethering",
            modifier = Modifier.size(64.dp),
        )
    }
}
