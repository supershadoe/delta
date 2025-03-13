package dev.shadoe.delta.settings.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.DesktopAccessDisabled
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import dev.shadoe.delta.R

@Composable
internal fun MaxClientLimitField(
  allowedLimit: Int,
  maxClient: Int,
  onMaxClientChange: (Float) -> Unit,
) {
  Row(
    modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp),
    verticalAlignment = Alignment.CenterVertically,
  ) {
    Icon(
      imageVector = Icons.Rounded.DesktopAccessDisabled,
      contentDescription = stringResource(R.string.max_client_limit_field_icon),
    )
    Column(
      modifier = Modifier.weight(1f).padding(horizontal = 16.dp),
      horizontalAlignment = Alignment.Start,
    ) {
      Text(
        text = stringResource(R.string.max_client_limit_field_label),
        style = MaterialTheme.typography.titleLarge,
      )
      Slider(
        value = allowedLimit.toFloat(),
        onValueChange = { onMaxClientChange(it) },
        valueRange = 1f..maxClient.toFloat(),
        steps = maxClient.floorDiv(5),
        colors =
          SliderDefaults.colors(
            thumbColor = MaterialTheme.colorScheme.secondary,
            activeTrackColor = MaterialTheme.colorScheme.secondary,
            inactiveTrackColor = MaterialTheme.colorScheme.secondaryContainer,
          ),
      )
      Text(
        text =
          stringResource(R.string.max_client_limit_field_count, allowedLimit),
        modifier = Modifier.align(alignment = Alignment.CenterHorizontally),
        color = MaterialTheme.colorScheme.onSurfaceVariant,
      )
    }
  }
}
