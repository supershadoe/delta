package dev.shadoe.delta.settings.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Error
import androidx.compose.material.icons.rounded.DesktopAccessDisabled
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import dev.shadoe.delta.R
import dev.shadoe.delta.common.components.Spinbox

@Composable
internal fun MaxClientLimitField(
  currentLimit: Int,
  maxClient: Int,
  onMaxClientChange: (Int?) -> Unit,
) {
  var isBlank by remember { mutableStateOf(false) }
  Row(
    verticalAlignment = Alignment.CenterVertically,
    modifier = Modifier.padding(vertical = 8.dp),
  ) {
    Icon(
      imageVector = Icons.Rounded.DesktopAccessDisabled,
      contentDescription = stringResource(R.string.max_client_limit_field_icon),
    )
    Column(modifier = Modifier.padding(horizontal = 16.dp).fillMaxWidth()) {
      Spinbox(
        currentValue = if (isBlank) null else currentLimit,
        min = 1,
        max = maxClient,
        onValueChanged = {
          isBlank = it == null
          onMaxClientChange(it)
        },
        label = {
          Text(text = stringResource(R.string.max_client_limit_field_label))
        },
      )
      if (isBlank) {
        Row(modifier = Modifier.padding(top = 8.dp)) {
          Icon(
            imageVector = Icons.Outlined.Error,
            contentDescription = stringResource(R.string.error_icon),
            tint = MaterialTheme.colorScheme.error,
          )
          Text(
            text = stringResource(R.string.max_client_limit_field_empty),
            color = MaterialTheme.colorScheme.error,
            modifier = Modifier.padding(start = 4.dp),
          )
        }
      }
      Text(
        modifier = Modifier.padding(top = 8.dp),
        text = stringResource(R.string.max_client_limit_field_desc, maxClient),
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
      )
    }
  }
}
