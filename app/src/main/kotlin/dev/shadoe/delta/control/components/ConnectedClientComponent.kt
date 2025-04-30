package dev.shadoe.delta.control.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ContentCopy
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import dev.shadoe.delta.R
import dev.shadoe.delta.api.TetheredClient

data class ConnectedClientComponentState(
  val client: TetheredClient,
  val isEditingBlocklist: Boolean,
  val supportsBlocklist: Boolean,
  val isChosenForBlocking: Boolean,
)

data class ConnectedClientComponentActions(
  val startEditing: () -> Unit,
  val onEditToggled: (Boolean) -> Unit,
)

@Composable
fun ConnectedClientComponent(
  state: ConnectedClientComponentState,
  actions: ConnectedClientComponentActions,
) {
  val clipboardManager = LocalClipboardManager.current
  val density = LocalDensity.current
  Row(
    modifier =
      Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 4.dp),
    verticalAlignment = Alignment.CenterVertically,
  ) {
    if (state.isEditingBlocklist) {
      Checkbox(
        checked = state.isChosenForBlocking,
        onCheckedChange = actions.onEditToggled,
      )
    }
    Column(modifier = Modifier.weight(1f)) {
      Text(
        text =
          state.client.hostname ?: stringResource(R.string.no_client_hostname)
      )
      state.client.address?.address?.hostAddress.let { ip ->
        Row(
          horizontalArrangement = Arrangement.spacedBy(8.dp),
          verticalAlignment = Alignment.CenterVertically,
          modifier =
            Modifier.clickable {
              ip ?: return@clickable
              clipboardManager.setText(AnnotatedString(ip))
            },
        ) {
          Text(text = ip ?: stringResource(R.string.ip_not_allocated))
          ip ?: return@Row
          Icon(
            imageVector = Icons.Rounded.ContentCopy,
            contentDescription = stringResource(R.string.copy_button),
            tint = MaterialTheme.colorScheme.primary,
            modifier =
              Modifier.size(
                with(density) { LocalTextStyle.current.fontSize.toDp() }
              ),
          )
        }
      }
    }
    if (state.supportsBlocklist && !state.isEditingBlocklist) {
      Button(onClick = actions.startEditing) {
        Text(text = stringResource(R.string.block_button))
      }
    }
  }
}
