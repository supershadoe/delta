package dev.shadoe.delta.settings.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
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
import dev.shadoe.delta.api.SoftApAutoShutdownTimeout.AutoShutdownTimeoutType
import dev.shadoe.delta.api.SoftApAutoShutdownTimeout.DEFAULT
import dev.shadoe.delta.api.SoftApAutoShutdownTimeout.FIVE_MINUTES
import dev.shadoe.delta.api.SoftApAutoShutdownTimeout.ONE_HOUR
import dev.shadoe.delta.api.SoftApAutoShutdownTimeout.TEN_MINUTES
import dev.shadoe.delta.api.SoftApAutoShutdownTimeout.THIRTY_MINUTES
import dev.shadoe.delta.api.SoftApAutoShutdownTimeout.TWENTY_MINUTES

@Composable
internal fun AutoShutDownTimeOutField(
  @AutoShutdownTimeoutType autoShutDownTimeOut: Long,
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
        items(supportedAutoShutdownType) {
          FilterChip(
            selected = autoShutDownTimeOut == it,
            onClick = { onAutoShutdownChange(it) },
            label = {
              Text(
                text =
                  stringResource(
                    when (it) {
                      DEFAULT -> R.string.auto_shutdown_default
                      FIVE_MINUTES -> R.string.auto_shutdown_5
                      TEN_MINUTES -> R.string.auto_shutdown_10
                      TWENTY_MINUTES -> R.string.auto_shutdown_20
                      THIRTY_MINUTES -> R.string.auto_shutdown_30
                      ONE_HOUR -> R.string.auto_shutdown_60
                      else -> R.string.auto_shutdown_default
                    }
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
