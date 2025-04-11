package dev.shadoe.delta.settings.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.History
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import dev.shadoe.delta.R

@Composable
internal fun PresetField(onShowPresets: () -> Unit, onSaveConfig: () -> Unit) {
  Row(
    modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp),
    verticalAlignment = Alignment.CenterVertically,
  ) {
    Icon(
      imageVector = Icons.Rounded.History,
      contentDescription = stringResource(R.string.presets_field_icon),
    )
    Column(
      modifier = Modifier.weight(1f).padding(horizontal = 16.dp),
      horizontalAlignment = Alignment.Start,
    ) {
      Text(
        text = stringResource(R.string.presets_setting),
        style = MaterialTheme.typography.titleLarge,
      )
      Text(
        text = stringResource(R.string.presets_field_desc),
        modifier = Modifier.padding(vertical = 4.dp),
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
      )
      Row {
        Button(onClick = onShowPresets) {
          Text(text = stringResource(R.string.presets_show_button))
        }
        Button(
          modifier = Modifier.padding(start = 8.dp),
          onClick = onSaveConfig,
        ) {
          Text(text = stringResource(R.string.presets_save_config_button))
        }
      }
    }
  }
}
