package dev.shadoe.delta.hotspot

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
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
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import dev.shadoe.delta.hotspot.buttons.HotspotButton
import dev.shadoe.delta.hotspot.components.ConnectedDevicesList
import dev.shadoe.delta.hotspot.components.PasswordDisplay
import dev.shadoe.delta.hotspot.navigation.LocalNavController
import dev.shadoe.delta.hotspot.navigation.Routes
import dev.shadoe.hotspotapi.WifiApEnabledStates
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HotspotScreen() {
    val sheetState = rememberModalBottomSheetState()
    val showConnectedDevices = remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    val navController = LocalNavController.current
    val isBigScreen = LocalConfiguration.current.screenWidthDp >= 700
    // TODO: remove this comment
//    println("isBigScreen: $isBigScreen; screenWidthDp: ${LocalConfiguration.current.screenWidthDp}")

    val hotspotApi = LocalHotspotApiInstance.current!!
    val ssid = hotspotApi.ssid.collectAsState(null)
    val password = hotspotApi.passphrase.collectAsState(null)
    val tetheredClients = hotspotApi.tetheredClients.collectAsState(emptyList())
    val enabledState = hotspotApi.enabledState.collectAsState()

    Row {
        Scaffold(
            modifier = Modifier.weight(1f),
            topBar = {
                CenterAlignedTopAppBar(title = {
                    Text(text = "Delta")
                })
            },
            floatingActionButton = {
                FloatingActionButton(onClick = {
                    navController?.navigate(route = Routes.HotspotEditScreen)
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
                modifier = Modifier.padding(scaffoldPadding),
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
                    Text(text = ssid.value ?: "no ssid")
                    Box(modifier = Modifier.padding(bottom = 16.dp)) {
                        PasswordDisplay(password = password.value)
                    }
                    if (!isBigScreen) {
                        TextButton(onClick = {
                            if (enabledState.value == WifiApEnabledStates.WIFI_AP_STATE_ENABLED) {
                                if (tetheredClients.value.isEmpty()) {
                                    if (snackbarHostState.currentSnackbarData == null) {
                                        scope.launch {
                                            snackbarHostState.showSnackbar(
                                                message = "There are no connected devices.",
                                                withDismissAction = true,
                                                duration = SnackbarDuration.Short
                                            )
                                        }
                                    }
                                } else {
                                    showConnectedDevices.value = true
                                }
                            } else if (snackbarHostState.currentSnackbarData == null) {
                                scope.launch {
                                    val result = snackbarHostState.showSnackbar(
                                        message = "Hotspot is not yet enabled.",
                                        actionLabel = "ENABLE",
                                        withDismissAction = true,
                                        duration = SnackbarDuration.Short,
                                    )
                                    if (result == SnackbarResult.ActionPerformed) {
                                        hotspotApi.startHotspot()
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
        if (isBigScreen) {
            Scaffold(modifier = Modifier.weight(1f)) {
                Box(
                    modifier = Modifier
                        .background(MaterialTheme.colorScheme.surfaceContainerLowest)
                        .padding(it)
                        .fillMaxHeight(),
                ) {
                    ConnectedDevicesList(tetheredClients.value)
                }
            }
        }
    }
}
