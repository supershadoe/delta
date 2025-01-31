package dev.shadoe.delta.hotspot

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextDecoration
import dev.shadoe.delta.hotspot.buttons.HotspotButton
import dev.shadoe.delta.hotspot.components.ConnectedDevicesList
import dev.shadoe.delta.hotspot.components.HotspotNotEnabledSnackbar
import dev.shadoe.delta.hotspot.components.PasswordDisplay
import dev.shadoe.hotspotapi.WifiApEnabledStates
import kotlinx.coroutines.launch

@Composable
fun HotspotScreen() {
    HotspotApiScope {
        HomeScreenUi()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HomeScreenUi() {
    val sheetState = rememberModalBottomSheetState()
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
                HotspotButton(
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
                PasswordDisplay(password = password.value)
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
                    ConnectedDevicesList(tetheredClients.value)
                }
            }
        }
    }
}