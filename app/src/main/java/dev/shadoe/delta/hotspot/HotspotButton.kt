package dev.shadoe.delta.hotspot

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp

@Composable
internal fun HotspotButton(
    shape: Shape,
    interactionSource: MutableInteractionSource,
    isEnabled: Boolean,
    isLoading: Boolean,
    onClick: () -> Unit,
    content: @Composable BoxScope.() -> Unit
) {
    val color = if (isLoading) {
        Color(color = 0x55E79AF3)
    } else if (isEnabled) {
        Color(color = 0xAAE79AF3)
    } else {
        Color(color = 0x77E79AF3)
    }
    Box(
        modifier = Modifier
            .size(200.dp)
            .padding(16.dp)
            .clip(shape = shape)
            .background(color)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            ),
        contentAlignment = Alignment.Center,
        content = content,
    )
}