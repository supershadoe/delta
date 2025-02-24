package dev.shadoe.delta.hotspot.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Visibility
import androidx.compose.material.icons.rounded.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider

@Preview
@Composable
internal fun PasswordDisplay(
    @PreviewParameter(provider = PasswordProvider::class) password: String?,
) {
    val isPasswordShown = remember { mutableStateOf(false) }

    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(modifier = Modifier.weight(1f)) {}
        Text(
            modifier = Modifier.weight(2f),
            text =
                if (password == null) {
                    "(no password)"
                } else if (isPasswordShown.value) {
                    password
                } else {
                    "(password hidden)"
                },
            style =
                TextStyle(
                    fontStyle =
                        if (isPasswordShown.value && password != null) {
                            FontStyle.Normal
                        } else {
                            FontStyle.Italic
                        },
                ),
        )
        IconButton(
            onClick = {
                isPasswordShown.value = !isPasswordShown.value
            },
        ) {
            Icon(
                imageVector =
                    if (isPasswordShown.value) {
                        Icons.Rounded.VisibilityOff
                    } else {
                        Icons.Rounded.Visibility
                    },
                contentDescription = "Show password",
            )
        }
        Box(modifier = Modifier.weight(1f)) {}
    }
}

private class PasswordProvider : PreviewParameterProvider<String?> {
    override val values: Sequence<String?>
        get() = sequenceOf("somepass123", null)
}
