package dev.shadoe.delta.control.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Visibility
import androidx.compose.material.icons.rounded.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import dev.shadoe.delta.R

@Composable
internal fun PassphraseDisplay(passphrase: String?) {
  val density = LocalDensity.current
  val isPassphraseShown = remember { mutableStateOf(false) }

  Row(
    modifier =
      Modifier.clickable { isPassphraseShown.value = !isPassphraseShown.value },
    verticalAlignment = Alignment.CenterVertically,
  ) {
    Text(
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
            }
        ),
    )
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
      modifier =
        Modifier.padding(start = 8.dp)
          .size(with(density) { LocalTextStyle.current.fontSize.toDp() }),
    )
  }
}
