package dev.shadoe.delta.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.Visibility
import androidx.compose.material.icons.rounded.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.shadoe.delta.hotspot.ControlButton
import dev.shadoe.delta.hotspot.HotspotApiScope
import dev.shadoe.delta.hotspot.HotspotNotEnabledSnackbar
import dev.shadoe.delta.hotspot.LocalHotspotApiInstance
import dev.shadoe.hotspotapi.WifiApEnabledStates
import kotlinx.coroutines.launch

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
    val sheetState = rememberModalBottomSheetState()
    val isPasswordShown = remember { mutableStateOf(false) }
    val showConnectedDevices = remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    val hotspotApi = LocalHotspotApiInstance.current!!
    val ssid = remember { mutableStateOf(hotspotApi.ssid ?: "") }
    val password = remember {
        mutableStateOf(hotspotApi.passphrase ?: "")
    }
    val tetheredClients = hotspotApi.tetheredClients.collectAsState(emptyList())
    val enabledState = hotspotApi.enabledState.collectAsState()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(title = {
                Text(text = "Delta")
            })
        },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                scope.launch {
                    snackbarHostState.showSnackbar(
                        message = "This functionality hasn't been added yet.",
                        withDismissAction = true,
                        duration = SnackbarDuration.Short,
                    )
                }
            }) {
                Icon(
                    imageVector = Icons.Rounded.Edit,
                    contentDescription = "Edit"
                )
            }
        },
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        },
    ) { scaffoldPadding ->
        Column(
            modifier = Modifier
                .padding(scaffoldPadding)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Box(
                modifier = Modifier.weight(1f),
                contentAlignment = Alignment.Center
            ) {
                ControlButton(
                    enabledState = enabledState.value,
                    startHotspot = { hotspotApi.startHotspot() },
                    stopHotspot = { hotspotApi.stopHotspot() },
                )
            }
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Bottom,
            ) {
                Text(text = ssid.value)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        modifier = Modifier.width((LocalConfiguration.current.screenWidthDp / 2).dp),
                        text = if (isPasswordShown.value) {
                            password.value
                        } else {
                            "(password hidden)"
                        },
                        style = TextStyle(
                            fontStyle = if (isPasswordShown.value) {
                                FontStyle.Normal
                            } else {
                                FontStyle.Italic
                            }
                        )
                    )
                    IconButton(
                        onClick = {
                            isPasswordShown.value = !isPasswordShown.value
                        },
                    ) {
                        Icon(
                            imageVector = if (isPasswordShown.value) {
                                Icons.Rounded.VisibilityOff
                            } else {
                                Icons.Rounded.Visibility
                            },
                            contentDescription = "Show password",
                        )
                    }
                }
                TextButton(onClick = {
                    if (enabledState.value == WifiApEnabledStates.WIFI_AP_STATE_ENABLED) {
                        showConnectedDevices.value = true
                    } else if (snackbarHostState.currentSnackbarData == null) {
                        scope.launch {
                            val result = snackbarHostState.showSnackbar(
                                visuals = HotspotNotEnabledSnackbar(),
                            )
                            when (result) {
                                SnackbarResult.ActionPerformed -> {
                                    hotspotApi.startHotspot()
                                }

                                SnackbarResult.Dismissed -> {}
                            }
                        }
                    }
                }) {
                    Text(
                        "Connected Devices (${tetheredClients.value.size})",
                        style = TextStyle(textDecoration = TextDecoration.Underline)
                    )
                }
            }
            if (showConnectedDevices.value) {
                ModalBottomSheet(
                    onDismissRequest = {
                        showConnectedDevices.value = false
                    },
                    sheetState = sheetState,
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .padding(horizontal = 24.dp)
                            .fillMaxWidth()
                    ) {
                        Text(
                            text = "Connected Devices",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                        LazyColumn(
                            contentPadding = PaddingValues(vertical = 32.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp),
                        ) {
                            if (tetheredClients.value.isEmpty()) {
                                item {
                                    Text(
                                        text = "No devices connected",
                                        modifier = Modifier.padding(vertical = 32.dp)
                                    )
                                }
                            }
                            items(tetheredClients.value.size) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(
                                        modifier = Modifier.padding(
                                            horizontal = 8.dp
                                        )
                                    ) {
                                        with(tetheredClients.value[it].addresses.firstOrNull()) {
                                            Text(
                                                text = this?.hostname
                                                    ?: "No name"
                                            )
                                            Text(
                                                text = this?.address?.address?.hostAddress
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
            }
        }
    }
}