package dev.shadoe.delta.hotspot.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import dev.shadoe.delta.R
import dev.shadoe.delta.api.ACLDevice
import dev.shadoe.delta.presentation.hotspot.ConnectedDevicesViewModel

@Composable
internal fun ConnectedDevicesList(vm: ConnectedDevicesViewModel = viewModel()) {
    val tetheredClients by vm.connectedClients.collectAsState(emptyList())

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier =
            Modifier
                .padding(horizontal = 24.dp, vertical = 16.dp)
                .fillMaxWidth(),
    ) {
        Text(
            text = stringResource(R.string.connected_devices),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.titleLarge,
        )
        if (tetheredClients.isEmpty()) {
            Box(
                modifier = Modifier.weight(1f),
                contentAlignment = Alignment.Center,
            ) {
                Text(text = stringResource(R.string.no_connected_devices))
            }
        }
        LazyColumn(
            contentPadding = PaddingValues(vertical = 32.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            items(tetheredClients.size) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    with(tetheredClients[it]) {
                        Column(
                            modifier =
                                Modifier.padding(
                                    horizontal = 8.dp,
                                ),
                        ) {
                            Text(
                                text =
                                    hostname
                                        ?: stringResource(
                                            R.string.no_client_hostname,
                                        ),
                            )
                            Text(
                                text =
                                    address
                                        ?.address
                                        ?.hostAddress
                                        ?: stringResource(
                                            R.string.ip_not_allocated,
                                        ),
                            )
                        }
                        Button(onClick = {
                            vm.blockDevice(
                                device =
                                    ACLDevice(
                                        hostname = hostname,
                                        macAddress = macAddress,
                                    ),
                            )
                        }) {
                            Text(text = stringResource(R.string.block_button))
                        }
                    }
                }
            }
        }
    }
}
