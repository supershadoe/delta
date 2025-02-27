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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import dev.shadoe.delta.R
import dev.shadoe.delta.hotspot.LocalHotspotApiInstance
import dev.shadoe.delta.hotspot.setSoftApConfiguration
import dev.shadoe.hotspotapi.helper.BlockedDevice
import dev.shadoe.hotspotapi.helper.TetheredClientWrapper
import kotlinx.coroutines.launch

@Composable
internal fun ConnectedDevicesList(
    tetheredClients: List<TetheredClientWrapper>,
) {
    val hotspotApi = LocalHotspotApiInstance.current!!
    val config by hotspotApi.config.collectAsState()
    val scope = rememberCoroutineScope()
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
                                    hostnames.firstOrNull()
                                        ?: stringResource(
                                            R.string.no_client_hostname,
                                        ),
                            )
                            Text(
                                text =
                                    addresses
                                        .firstOrNull()
                                        ?.address
                                        ?.hostAddress
                                        ?: stringResource(
                                            R.string.ip_not_allocated,
                                        ),
                            )
                        }
                        Button(onClick = {
                            scope.launch {
                                val d =
                                    config.blockedDevices +
                                        BlockedDevice(
                                            hostname = hostnames.firstOrNull(),
                                            macAddress = macAddress,
                                        )
                                setSoftApConfiguration(
                                    hotspotApi = hotspotApi,
                                    config =
                                        config.copy(
                                            blockedDevices = d,
                                        ),
                                )
                            }
                        }) {
                            Text(text = stringResource(R.string.block_button))
                        }
                    }
                }
            }
        }
    }
}
