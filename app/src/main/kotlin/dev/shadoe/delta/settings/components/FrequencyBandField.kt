package dev.shadoe.delta.settings.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
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
import dev.shadoe.delta.api.SoftApSpeedType.BAND_2GHZ
import dev.shadoe.delta.api.SoftApSpeedType.BAND_5GHZ
import dev.shadoe.delta.api.SoftApSpeedType.BAND_60GHZ
import dev.shadoe.delta.api.SoftApSpeedType.BAND_6GHZ
import dev.shadoe.delta.api.SoftApSpeedType.BandType

@Composable
internal fun FrequencyBandField(
  @BandType frequencyBand: Int,
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
        items(supportedBands) {
          FilterChip(
            selected = frequencyBand == it,
            onClick = { onBandChange(it) },
            label = {
              Text(
                text =
                  stringResource(
                    when (it) {
                      BAND_2GHZ -> R.string.freq_band_2_4_GHz
                      BAND_5GHZ -> R.string.freq_band_5_GHz
                      BAND_6GHZ -> R.string.freq_band_6_GHz
                      BAND_60GHZ -> R.string.freq_band_60_GHz
                      else -> R.string.freq_band_unknown
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
            when (frequencyBand) {
              BAND_2GHZ -> R.string.freq_band_2_4_GHz_desc
              BAND_5GHZ -> R.string.freq_band_5_GHz_desc
              BAND_6GHZ -> R.string.freq_band_6_GHz_desc
              BAND_60GHZ -> R.string.freq_band_60_GHz_desc
              else -> R.string.freq_band_unknown
            }
          ),
        modifier = Modifier.padding(start = 8.dp),
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
      )
    }
  }
}
