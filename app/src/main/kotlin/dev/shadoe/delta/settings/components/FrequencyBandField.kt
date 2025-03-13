package dev.shadoe.delta.settings.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.NetworkWifi
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
import dev.shadoe.delta.api.SoftApSpeedType.getResOfSpeedType

@Composable
internal fun FrequencyBandField(
  frequencyBand: Int,
  supportedBands: List<Int>,
  onBandChange: (Int) -> Unit,
) {
  Row(
    verticalAlignment = Alignment.CenterVertically,
    modifier = Modifier.padding(vertical = 8.dp),
  ) {
    Icon(
      imageVector = Icons.Rounded.NetworkWifi,
      contentDescription = stringResource(R.string.freq_band_field_icon),
    )
    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
      Text(
        text = stringResource(R.string.freq_band_field_label),
        modifier = Modifier.padding(start = 8.dp),
        style = MaterialTheme.typography.titleMedium,
      )
      LazyRow {
        items(supportedBands.size) {
          FilterChip(
            selected = frequencyBand == supportedBands[it],
            onClick = { onBandChange(supportedBands[it]) },
            label = {
              Text(text = stringResource(getResOfSpeedType(supportedBands[it])))
            },
            modifier = Modifier.padding(horizontal = 2.dp),
          )
        }
      }
    }
  }
}
