package dev.shadoe.delta.hotspot.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Visibility
import androidx.compose.material.icons.rounded.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import dev.shadoe.delta.R

@Preview
@Composable
internal fun PassphraseDisplay(
    @PreviewParameter(provider = PassphraseProvider::class) passphrase: String?,
) {
    val isPassphraseShown = remember { mutableStateOf(false) }

    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(modifier = Modifier.weight(1f)) {}
        Text(
            modifier = Modifier.weight(2f),
            text =
                if (passphrase == null) {
                    stringResource(R.string.no_passphrase)
                } else if (isPassphraseShown.value) {
                    passphrase
                } else {
                    stringResource(R.string.passphrase_hidden)
                },
            style =
                MaterialTheme.typography.bodyMedium.copy(
                    fontStyle =
                        if (isPassphraseShown.value && passphrase != null) {
                            FontStyle.Normal
                        } else {
                            FontStyle.Italic
                        },
                ),
        )
        IconButton(
            onClick = {
                isPassphraseShown.value = !isPassphraseShown.value
            },
        ) {
            Icon(
                imageVector =
                    if (isPassphraseShown.value) {
                        Icons.Rounded.VisibilityOff
                    } else {
                        Icons.Rounded.Visibility
                    },
                contentDescription =
                    if (isPassphraseShown.value) {
                        stringResource(R.string.passphrase_hide)
                    } else {
                        stringResource(R.string.passphrase_show)
                    },
            )
        }
        Box(modifier = Modifier.weight(1f)) {}
    }
}

private class PassphraseProvider : PreviewParameterProvider<String?> {
    override val values: Sequence<String?>
        get() = sequenceOf("somepass123", null)
}
