package dev.shadoe.delta.hotspot

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.min

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
        MaterialTheme.colorScheme.tertiaryContainer
    } else if (isEnabled) {
        MaterialTheme.colorScheme.primaryContainer
    } else {
        MaterialTheme.colorScheme.surfaceBright
    }
    val description = if (isLoading) {
        "Loading"
    } else if (isEnabled) {
        "Stop hotspot"
    } else {
        "Start hotspot"
    }
    Box(
        modifier = Modifier
            .size(
                min(
                    LocalConfiguration.current.screenWidthDp.dp,
                    LocalConfiguration.current.screenHeightDp.dp
                ) / 2
            )
            .padding(16.dp)
            .clip(shape = shape)
            .background(color)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            )
            .semantics(
                mergeDescendants = true,
                properties = {
                    contentDescription = description
                },
            ),
        contentAlignment = Alignment.Center,
        content = content,
    )
}