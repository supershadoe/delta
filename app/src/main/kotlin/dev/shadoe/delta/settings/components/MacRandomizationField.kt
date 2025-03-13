package dev.shadoe.delta.settings.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Shuffle
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import dev.shadoe.delta.R
import dev.shadoe.delta.api.SoftApRandomizationSetting.RANDOMIZATION_NONE
import dev.shadoe.delta.api.SoftApRandomizationSetting.RANDOMIZATION_NON_PERSISTENT
import dev.shadoe.delta.api.SoftApRandomizationSetting.RANDOMIZATION_PERSISTENT
import dev.shadoe.delta.api.SoftApRandomizationSetting.RandomizationType
import dev.shadoe.delta.api.SoftApRandomizationSetting.supportedRandomizationSettings

@Composable
internal fun MacRandomizationField(
  @RandomizationType macRandomizationSetting: Int,
  onSettingChange: (Int) -> Unit,
) {
  Row(
    modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp),
    verticalAlignment = Alignment.CenterVertically,
  ) {
    Icon(
      imageVector = Icons.Rounded.Shuffle,
      contentDescription = stringResource(R.string.mac_randomization_field_icon),
    )
    Column(
      modifier = Modifier.weight(1f).padding(horizontal = 16.dp),
      horizontalAlignment = Alignment.Start,
    ) {
      Text(
        text = stringResource(R.string.mac_randomization_field_label),
        style = MaterialTheme.typography.titleLarge,
      )
      LazyRow {
        items(supportedRandomizationSettings) {
          FilterChip(
            selected = macRandomizationSetting == it,
            onClick = { onSettingChange(it) },
            label = {
              Text(
                stringResource(
                  when (it) {
                    RANDOMIZATION_NONE -> R.string.mac_randomization_none
                    RANDOMIZATION_PERSISTENT ->
                      R.string.mac_randomization_persistent
                    RANDOMIZATION_NON_PERSISTENT ->
                      R.string.mac_randomization_non_persistent
                    else -> R.string.mac_randomization_none
                  }
                )
              )
            },
            modifier = Modifier.padding(horizontal = 2.dp),
          )
        }
      }
      Text(
        text =
          stringResource(
            when (macRandomizationSetting) {
              RANDOMIZATION_NONE -> R.string.mac_randomization_none_desc
              RANDOMIZATION_PERSISTENT ->
                R.string.mac_randomization_persistent_desc
              RANDOMIZATION_NON_PERSISTENT ->
                R.string.mac_randomization_non_persistent_desc
              else -> R.string.mac_randomization_none
            }
          ),
        modifier = Modifier.padding(start = 8.dp),
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
      )
    }
  }
}
