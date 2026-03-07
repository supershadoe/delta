package dev.shadoe.delta.settings.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Radio
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
import dev.shadoe.delta.api.SoftApSpeedType
import dev.shadoe.delta.api.SoftApSpeedType.BandType
import android.util.Log

// Common channels for each band (global/regulatory domain agnostic)
// 2.4GHz: Channels 1-14 (14 is Japan only)
val CHANNELS_2GHZ = listOf(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14)
// 5GHz: All possible channels (UNII-1, UNII-2A, UNII-2C, UNII-3)
// Actual availability depends on device regulatory domain
val CHANNELS_5GHZ = listOf(0, 36, 40, 44, 48, 52, 56, 60, 64, 100, 104, 108, 112, 116, 120, 124, 128, 132, 136, 140, 144, 149, 153, 157, 161, 165)
// 6GHz: All possible 6GHz channels
val CHANNELS_6GHZ = listOf(0, 1, 5, 9, 13, 17, 21, 25, 29, 33, 37, 41, 45, 49, 53, 57, 61, 65, 69, 73, 77, 81, 85, 89, 93, 97, 101, 105, 109, 113, 117, 121, 125, 129, 133, 137, 141, 145, 149, 153, 157, 161, 165, 169, 173, 177, 181, 185, 189, 193, 197, 201, 205, 209, 213, 217, 221, 225, 229, 233)

@Composable
internal fun ChannelField(
  @BandType bandType: Int,
  currentChannel: Int,
  supportedChannels: List<Int>,
  onChannelChange: (Int) -> Unit,
) {
  // If supportedChannels is empty, fall back to default channels
  val defaultChannels = when (bandType) {
    SoftApSpeedType.BAND_2GHZ -> CHANNELS_2GHZ
    SoftApSpeedType.BAND_5GHZ -> CHANNELS_5GHZ
    SoftApSpeedType.BAND_6GHZ -> CHANNELS_6GHZ
    else -> listOf(0) // Auto for unknown bands
  }
  
  // Filter to only show supported channels, but always include 0 (Auto)
  val channels = if (supportedChannels.isEmpty()) {
    defaultChannels
  } else {
    listOf(0) + supportedChannels.filter { it > 0 }.sorted()
  }

  Row(
    verticalAlignment = Alignment.CenterVertically,
    modifier = Modifier.padding(vertical = 8.dp),
  ) {
    Icon(
      imageVector = Icons.Rounded.Radio,
      contentDescription = stringResource(R.string.channel_field_icon),
    )
    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
      Text(
        text = stringResource(R.string.channel_field_label),
        style = MaterialTheme.typography.titleMedium,
      )
      LazyRow {
        items(channels) { channel ->
          FilterChip(
            selected = currentChannel == channel,
            onClick = { onChannelChange(channel) },
            label = {
              Text(
                text = if (channel == 0) {
                  stringResource(R.string.channel_auto)
                } else {
                  channel.toString()
                }
              )
            },
            modifier = Modifier.padding(horizontal = 2.dp),
          )
        }
      }
      Text(
        text = stringResource(
          if (currentChannel == 0) R.string.channel_auto_desc
          else R.string.channel_manual_desc,
          currentChannel
        ),
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
      )
    }
  }
}