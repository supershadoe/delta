package dev.shadoe.delta.hotspot

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Block
import androidx.compose.material.icons.rounded.Edit
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import dev.shadoe.delta.R
import dev.shadoe.delta.hotspot.buttons.HotspotButton
import dev.shadoe.delta.hotspot.components.ConnectedDevicesList
import dev.shadoe.delta.hotspot.components.PasswordDisplay
import dev.shadoe.delta.hotspot.navigation.LocalNavController
import dev.shadoe.delta.hotspot.navigation.Routes
import dev.shadoe.hotspotapi.helper.SoftApEnabledState
import dev.shadoe.hotspotapi.helper.SoftApSecurityType
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HotspotScreen(modifier: Modifier = Modifier) {
    val sheetState = rememberModalBottomSheetState()
    val showConnectedDevices = remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    val navController = LocalNavController.current
    val isBigScreen = LocalConfiguration.current.screenWidthDp >= 700
    // TODO: remove this comment
    println(
        "isBigScreen: $isBigScreen; screenWidthDp: ${LocalConfiguration.current.screenWidthDp}",
    )

    val hotspotApi = LocalHotspotApiInstance.current!!
    val config = hotspotApi.config.collectAsState()
    val tetheredClients = hotspotApi.tetheredClients.collectAsState(emptyList())
    val enabledState = hotspotApi.enabledState.collectAsState()

    val noConnectedDevicesText =
        stringResource(id = R.string.no_connected_devices)
    val hotspotNotEnabledText =
        stringResource(id = R.string.hotspot_not_enabled)
    val hotspotEnableActionText =
        stringResource(id = R.string.hotspot_enable_action)

    val onConnectedDevicesClicked = {
        if (enabledState.value == SoftApEnabledState.WIFI_AP_STATE_ENABLED) {
            if (tetheredClients.value.isEmpty()) {
                if (snackbarHostState.currentSnackbarData == null) {
                    scope.launch {
                        snackbarHostState.showSnackbar(
                            message = noConnectedDevicesText,
                            withDismissAction = true,
                            duration = SnackbarDuration.Short,
                        )
                    }
                }
            } else {
                showConnectedDevices.value = true
            }
        } else if (snackbarHostState.currentSnackbarData == null) {
            scope.launch {
                val result =
                    snackbarHostState.showSnackbar(
                        message = hotspotNotEnabledText,
                        actionLabel = hotspotEnableActionText,
                        withDismissAction = true,
                        duration = SnackbarDuration.Short,
                    )
                if (result == SnackbarResult.ActionPerformed) {
                    hotspotApi.startHotspot()
                }
            }
        }
    }

    Row(modifier = modifier) {
        Scaffold(
            modifier = Modifier.weight(1f),
            topBar = {
                CenterAlignedTopAppBar(
                    title = {
                        Text(text = stringResource(id = R.string.app_name))
                    },
                    actions = {
                        IconButton(onClick = {
                            navController?.navigate(Routes.BlocklistScreen)
                        }) {
                            Icon(
                                imageVector = Icons.Rounded.Block,
                                contentDescription =
                                    stringResource(id = R.string.blocklist),
                            )
                        }
                    },
                )
            },
            floatingActionButton = {
                FloatingActionButton(onClick = {
                    navController?.navigate(route = Routes.HotspotEditScreen)
                }) {
                    Icon(
                        imageVector = Icons.Rounded.Edit,
                        contentDescription =
                            stringResource(id = R.string.edit_button),
                    )
                }
            },
            snackbarHost = {
                SnackbarHost(hostState = snackbarHostState)
            },
        ) { scaffoldPadding ->
            Column(
                modifier = Modifier.padding(scaffoldPadding).fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                Box(
                    modifier = Modifier.weight(1f),
                    contentAlignment = Alignment.Center,
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
                    Text(
                        text = config.value.ssid
                            ?: stringResource(id = R.string.no_ssid),
                    )
                    Box(modifier = Modifier.padding(bottom = 16.dp)) {
                        if (config.value.securityType !=
                            SoftApSecurityType.SECURITY_TYPE_OPEN
                        ) {
                            PasswordDisplay(password = config.value.passphrase)
                        }
                    }
                    if (!isBigScreen) {
                        TextButton(onClick = onConnectedDevicesClicked) {
                            val style =
                                MaterialTheme.typography.bodyMedium.copy(
                                    textDecoration = TextDecoration.Underline,
                                )
                            Text(
                                stringResource(
                                    id = R.string.connected_devices,
                                    tetheredClients.value.size,
                                ),
                                style = style,
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
                val background =
                    MaterialTheme.colorScheme.surfaceContainerLowest
                Box(
                    modifier =
                        Modifier
                            .background(background)
                            .padding(it)
                            .fillMaxHeight(),
                ) {
                    ConnectedDevicesList(tetheredClients.value)
                }
            }
        }
    }
}
