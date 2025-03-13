package dev.shadoe.delta.settings.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
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

@Composable
internal fun MacRandomizationField(
  macRandomizationSetting: Int,
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

      val supportedMACRandomizationType =
        listOf(
          stringResource(R.string.mac_randomization_none),
          stringResource(R.string.mac_randomization_persistent),
          stringResource(R.string.mac_randomization_non_persistent),
        )
      LazyRow {
        items(supportedMACRandomizationType.size) {
          FilterChip(
            selected =
              supportedMACRandomizationType[macRandomizationSetting] ==
                supportedMACRandomizationType[it],
            onClick = { onSettingChange(it) },
            label = { Text(text = supportedMACRandomizationType[it]) },
            modifier = Modifier.padding(horizontal = 2.dp),
          )
        }
      }
    }
  }
}
