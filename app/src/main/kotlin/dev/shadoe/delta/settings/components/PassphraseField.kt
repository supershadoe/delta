package dev.shadoe.delta.settings.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Error
import androidx.compose.material.icons.rounded.Password
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import dev.shadoe.delta.R
import dev.shadoe.delta.data.softap.validators.PassphraseValidator

@Composable
internal fun PassphraseField(
  passphrase: String,
  onPassphraseChange: (String) -> Unit,
  currentResult: PassphraseValidator.Result,
) {
  Row(
    verticalAlignment = Alignment.CenterVertically,
    modifier = Modifier.padding(vertical = 8.dp),
  ) {
    Icon(
      imageVector = Icons.Rounded.Password,
      contentDescription = stringResource(R.string.passphrase_field_icon),
    )
    Column(modifier = Modifier.padding(horizontal = 16.dp).fillMaxWidth()) {
      OutlinedTextField(
        value = passphrase,
        onValueChange = { onPassphraseChange(it) },
        singleLine = true,
        keyboardOptions =
          KeyboardOptions(
            capitalization = KeyboardCapitalization.None,
            autoCorrectEnabled = false,
            keyboardType = KeyboardType.Password,
            imeAction = ImeAction.Done,
          ),
        modifier = Modifier.fillMaxWidth(),
        label = { Text(text = stringResource(R.string.passphrase_field_label)) },
      )
      if (currentResult !is PassphraseValidator.Result.Success) {
        Row(modifier = Modifier.padding(top = 8.dp)) {
          Icon(
            imageVector = Icons.Outlined.Error,
            contentDescription = stringResource(R.string.error_icon),
            tint = MaterialTheme.colorScheme.error,
          )
          Text(
            text =
              when (currentResult) {
                is PassphraseValidator.Result.PskTooShort ->
                  stringResource(R.string.passphrase_too_short_warning)
                is PassphraseValidator.Result.PskTooLong ->
                  stringResource(R.string.passphrase_too_long_warning)
                else -> ""
              },
            color = MaterialTheme.colorScheme.error,
            modifier = Modifier.padding(start = 4.dp),
          )
        }
      }
    }
  }
}
