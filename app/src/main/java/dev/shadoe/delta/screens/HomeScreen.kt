package dev.shadoe.delta.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Password
import androidx.compose.material.icons.rounded.Wifi
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.shadoe.delta.hotspot.HotspotApiScope
import dev.shadoe.delta.hotspot.LocalHotspotApiInstance

@Preview
@Composable
fun HomeScreen() {
    HotspotApiScope {
        HomeScreenUi()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
private fun HomeScreenUi() {
    val hotspotApi = LocalHotspotApiInstance.current!!
    val ssid = remember { mutableStateOf(hotspotApi.ssid ?: "") }
    val password = remember {
        mutableStateOf(hotspotApi.passphrase ?: "")
    }
    val tetheredClients = hotspotApi.tetheredClients.collectAsState(emptyList())
    val isHotspotRunning = hotspotApi.isHotspotRunning.collectAsState(false)
    val isHotspotDisabled = hotspotApi.isHotspotDisabled.collectAsState(true)

    Scaffold(
        topBar = {
            LargeTopAppBar(
                title = { Text(text = "Delta") })
        }) { scaffoldPadding ->
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .padding(scaffoldPadding)
        ) {
            EditWidget(
                onClickLabel = "Edit SSID",
                icon = Icons.Rounded.Wifi,
                text = "SSID",
                value = ssid.value
            ) {
                ssid.value = it
            }
            EditWidget(
                onClickLabel = "Edit Password",
                icon = Icons.Rounded.Password,
                text = "Password",
                maskValue = true,
                value = password.value,
                onSave = { password.value = it })
            if (isHotspotDisabled.value) Button(
                modifier = Modifier.padding(
                    start = 24.dp
                ), onClick = {
                    try {
                        hotspotApi.startHotspot()
                    } catch (e: Exception) {
                        println(e.stackTraceToString())
                    }
                }) {
                Text(text = "Start hotspot")
            }
            if (isHotspotRunning.value) Button(
                modifier = Modifier.padding(start = 24.dp), onClick = {
                    try {
                        hotspotApi.stopHotspot()
                    } catch (e: Exception) {
                        println(e.stackTraceToString())
                    }
                }) {
                Text(text = "Stop hotspot")
            }
            Column(
                modifier = Modifier.padding(
                    vertical = 16.dp, horizontal = 24.dp
                )
            ) {
                if (tetheredClients.value.isEmpty()) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .fillMaxHeight(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = if (isHotspotRunning.value) {
                                "No devices connected"
                            } else {
                                "Tap on \"Start Hotspot\" to start tethering to other devices"
                            }, textAlign = TextAlign.Center
                        )
                    }
                } else {
                    Text(
                        text = "Connected Devices",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    LazyColumn(
                        contentPadding = PaddingValues(vertical = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                    ) {
                        items(tetheredClients.value.size) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.padding(horizontal = 8.dp)) {
                                    with(tetheredClients.value[it]) {
                                        Text(
                                            text = addresses[0].hostname
                                                ?: "Unknown"
                                        )
                                        Text(text = macAddress.toString())
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
        }
    }
}

@Composable
private fun EditWidget(
    onClickLabel: String,
    icon: ImageVector,
    text: String,
    value: String,
    maskValue: Boolean = false,
    onSave: (String) -> Unit = {},
) {
    val isEditing = remember { mutableStateOf(false) }
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                onClickLabel = onClickLabel, role = Role.Button,
            ) {
                isEditing.value = true
            },
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                imageVector = icon, contentDescription = "Edit"
            )
            Column(modifier = Modifier.padding(start = 16.dp)) {
                Text(text = text)
                Text(text = if (maskValue) "********" else value)
            }
        }
    }
    if (isEditing.value) AlertDialog(
        onDismissRequest = { isEditing.value = false },
        title = { Text(text = "Edit Password") },
        text = {
            TextField(
                value = value, onValueChange = onSave
            )
        },
        confirmButton = {
            TextButton(onClick = { isEditing.value = false }) {
                Text(text = "Save")
            }
        },
    )
}
