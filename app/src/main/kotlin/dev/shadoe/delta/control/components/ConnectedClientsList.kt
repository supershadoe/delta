package dev.shadoe.delta.control.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import dev.shadoe.delta.R
import dev.shadoe.delta.api.ACLDevice
import dev.shadoe.delta.api.TetheredClient

fun LazyListScope.connectedClientsList(
  tetheredClients: List<TetheredClient>,
  supportsBlocklist: Boolean,
  onBlockClient: (ACLDevice) -> Unit,
) {
  item {
    Text(
      text = stringResource(R.string.connected_devices),
      color = MaterialTheme.colorScheme.onSurfaceVariant,
      style = MaterialTheme.typography.titleLarge,
      modifier = Modifier.padding(16.dp),
    )
  }
  if (tetheredClients.isEmpty()) {
    item {
      Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center,
      ) {
        Text(text = stringResource(R.string.no_connected_devices))
      }
    }
  }
  items(tetheredClients) { client ->
    ConnectedClientComponent(client, supportsBlocklist, onBlockClient)
  }
}
