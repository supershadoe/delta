package dev.shadoe.delta.control.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import dev.shadoe.delta.R
import dev.shadoe.delta.api.TetheredClient

internal data class ConnectedClientsListState(
  val tetheredClients: List<TetheredClient>,
  val supportsBlocklist: Boolean,
  val devicesToBlock: Set<TetheredClient>,
)

internal data class ConnectedClientsListActions(
  val addToBlockList: (TetheredClient) -> Unit,
  val removeFromBlockList: (TetheredClient) -> Unit,
  val onBlockClients: () -> Unit,
)

internal fun LazyListScope.connectedClientsList(
  state: ConnectedClientsListState,
  actions: ConnectedClientsListActions,
) {
  item {
    Text(
      text = stringResource(R.string.connected_devices),
      color = MaterialTheme.colorScheme.onSurfaceVariant,
      style = MaterialTheme.typography.titleLarge,
      modifier = Modifier.padding(16.dp),
    )
  }
  if (state.tetheredClients.isEmpty()) {
    item {
      Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center,
      ) {
        Text(text = stringResource(R.string.no_connected_devices))
      }
    }
  }
  items(state.tetheredClients) { client ->
    ConnectedClientComponent(
      state =
        ConnectedClientComponentState(
          client = client,
          isEditingBlocklist = state.devicesToBlock.isNotEmpty(),
          supportsBlocklist = state.supportsBlocklist,
          isChosenForBlocking = state.devicesToBlock.contains(client),
        ),
      actions =
        ConnectedClientComponentActions(
          startEditing = { actions.addToBlockList(client) },
          onEditToggled = { isChecked ->
            if (isChecked) {
              actions.addToBlockList(client)
            } else {
              actions.removeFromBlockList(client)
            }
          },
        ),
    )
  }
  if (state.devicesToBlock.isNotEmpty()) {
    item {
      Button(
        onClick = actions.onBlockClients,
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
      ) {
        Text(text = stringResource(R.string.block_button))
      }
    }
  }
}
