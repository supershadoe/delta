package dev.shadoe.delta.hotspot.buttons

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.WifiTetheringError
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.graphics.shapes.RoundedPolygon
import androidx.graphics.shapes.circle
import dev.shadoe.delta.utils.PolygonShape

@Composable
internal fun FailedButton() {
    ControlButton(
        shape = PolygonShape(RoundedPolygon.circle(numVertices = 6)),
        interactionSource = remember { MutableInteractionSource() },
        isEnabled = false,
        isLoading = true,
        onClick = {},
    ) {
        Icon(
            imageVector = Icons.Rounded.WifiTetheringError,
            contentDescription = "Tethering",
            modifier = Modifier.size(64.dp),
        )
    }
}
