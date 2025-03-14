package dev.shadoe.delta.settings.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Error
import androidx.compose.material.icons.rounded.Wifi
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
import dev.shadoe.delta.data.softap.validators.SsidValidator

@Composable
internal fun SsidField(
  ssid: String,
  onSsidChange: (String) -> Unit,
  currentResult: SsidValidator.Result,
) {
  Row(
    verticalAlignment = Alignment.CenterVertically,
    modifier = Modifier.padding(vertical = 8.dp),
  ) {
    Icon(
      imageVector = Icons.Rounded.Wifi,
      contentDescription = stringResource(R.string.ssid_field_icon),
    )
    Column(modifier = Modifier.padding(horizontal = 16.dp).fillMaxWidth()) {
      OutlinedTextField(
        value = ssid,
        onValueChange = { onSsidChange(it) },
        singleLine = true,
        keyboardOptions =
          KeyboardOptions(
            capitalization = KeyboardCapitalization.None,
            autoCorrectEnabled = false,
            keyboardType = KeyboardType.Text,
            imeAction = ImeAction.Next,
          ),
        modifier = Modifier.fillMaxWidth(),
        label = { Text(text = stringResource(R.string.ssid_field_label)) },
      )
      if (currentResult !is SsidValidator.Result.Success) {
        Row(modifier = Modifier.padding(top = 8.dp)) {
          Icon(
            imageVector = Icons.Outlined.Error,
            contentDescription = stringResource(R.string.error_icon),
            tint = MaterialTheme.colorScheme.error,
          )
          Text(
            text =
              when (currentResult) {
                is SsidValidator.Result.SsidTooShort ->
                  stringResource(R.string.ssid_too_short_warning)
                is SsidValidator.Result.SsidTooLong ->
                  stringResource(R.string.ssid_too_long_warning)
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
