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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.shadoe.hotspotapi.helper.TetheredClientWrapper

@Composable
internal fun ConnectedDevicesList(
    tetheredClients: List<TetheredClientWrapper>,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier =
            Modifier
                .padding(horizontal = 24.dp, vertical = 16.dp)
                .fillMaxWidth(),
    ) {
        Text(
            text = "Connected Devices",
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.titleLarge,
        )
        if (tetheredClients.isEmpty()) {
            Box(
                modifier = Modifier.weight(1f),
                contentAlignment = Alignment.Center,
            ) {
                Text(text = "No devices connected")
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
                    Column(
                        modifier =
                            Modifier.padding(
                                horizontal = 8.dp,
                            ),
                    ) {
                        with(tetheredClients[it]) {
                            Text(
                                text = hostnames.firstOrNull() ?: "No name",
                            )
                            Text(
                                text =
                                    addresses
                                        .firstOrNull()
                                        ?.address
                                        ?.hostAddress
                                        ?: "Link address not allocated",
                            )
                        }
                    }
                    Button(onClick = {}, enabled = false) {
                        Text(text = "BLOCK")
                    }
                }
            }
        }
    }
}
