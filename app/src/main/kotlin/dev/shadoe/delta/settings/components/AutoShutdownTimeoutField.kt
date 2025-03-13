package dev.shadoe.delta.settings.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Timer
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
import dev.shadoe.delta.api.SoftApAutoShutdownTimeout.getResOfTimeoutType

@Composable
internal fun AutoShutDownTimeOutField(
  autoShutDownTimeOut: Long,
  supportedAutoShutdownType: List<Long>,
  onAutoShutdownChange: (Long) -> Unit,
) {
  Row(
    modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp),
    verticalAlignment = Alignment.CenterVertically,
  ) {
    Icon(
      imageVector = Icons.Rounded.Timer,
      contentDescription =
        stringResource(R.string.auto_shutdown_timeout_field_icon),
    )
    Column(
      modifier = Modifier.weight(1f).padding(horizontal = 16.dp),
      horizontalAlignment = Alignment.Start,
    ) {
      Text(
        text = stringResource(R.string.auto_shutdown_timeout_field_label),
        style = MaterialTheme.typography.titleLarge,
      )
      LazyRow {
        items(supportedAutoShutdownType.size) {
          FilterChip(
            selected = autoShutDownTimeOut == supportedAutoShutdownType[it],
            onClick = { onAutoShutdownChange(supportedAutoShutdownType[it]) },
            label = {
              Text(
                text =
                  stringResource(
                    getResOfTimeoutType(supportedAutoShutdownType[it])
                  )
              )
            },
            modifier = Modifier.padding(horizontal = 2.dp),
          )
        }
      }
    }
  }
}
