package dev.shadoe.delta.control.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import dev.shadoe.delta.R
import dev.shadoe.delta.data.database.models.Preset

internal fun LazyListScope.presetsComponent(
  presets: List<Preset>,
  applyPreset: (Preset) -> Unit,
) {
  item {
    Text(
      text = stringResource(R.string.presets_setting),
      color = MaterialTheme.colorScheme.onSurfaceVariant,
      style = MaterialTheme.typography.titleLarge,
      modifier = Modifier.padding(16.dp),
    )
  }
  if (presets.isEmpty()) {
    item {
      Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center,
      ) {
        Text(text = stringResource(R.string.presets_none_saved))
      }
    }
  }
  items(presets) {
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically,
    ) {
      PresetComponent(it.presetName, applyPreset = { applyPreset(it) })
    }
  }
}
