package dev.shadoe.delta.control.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import dev.shadoe.delta.R
import dev.shadoe.delta.api.ACLDevice

data class BlockedClientComponentState(
  val device: ACLDevice,
  val isEditingBlocklist: Boolean,
  val isChosenForUnblock: Boolean,
)

data class BlockedClientComponentActions(
  val startEditing: () -> Unit,
  val onEditToggled: (Boolean) -> Unit,
)

@Composable
fun BlockedClientComponent(
  state: BlockedClientComponentState,
  actions: BlockedClientComponentActions,
  modifier: Modifier = Modifier,
) {
  Row(
    modifier =
      modifier.then(Modifier.padding(horizontal = 16.dp, vertical = 4.dp)),
    verticalAlignment = Alignment.CenterVertically,
  ) {
    if (state.isEditingBlocklist) {
      Checkbox(
        checked = state.isChosenForUnblock,
        onCheckedChange = actions.onEditToggled,
      )
    }
    Column(modifier = Modifier.weight(1f)) {
      Text(
        text =
          state.device.hostname ?: stringResource(R.string.no_client_hostname)
      )
      Text(text = state.device.macAddress.toString())
    }
    if (!state.isEditingBlocklist) {
      Button(onClick = actions.startEditing) {
        Text(text = stringResource(R.string.unblock_button))
      }
    }
  }
}
