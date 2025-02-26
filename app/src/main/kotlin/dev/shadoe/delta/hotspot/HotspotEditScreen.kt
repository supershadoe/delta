package dev.shadoe.delta.hotspot

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.NetworkWifi
import androidx.compose.material.icons.rounded.Password
import androidx.compose.material.icons.rounded.SettingsPower
import androidx.compose.material.icons.rounded.Wifi
import androidx.compose.material.icons.rounded.WifiPassword
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import dev.shadoe.delta.hotspot.navigation.LocalNavController
import dev.shadoe.hotspotapi.HotspotApi
import dev.shadoe.hotspotapi.SoftApConfiguration
import dev.shadoe.hotspotapi.helper.SoftApEnabledState
import dev.shadoe.hotspotapi.helper.SoftApSecurityType
import dev.shadoe.hotspotapi.helper.SoftApSecurityType.getNameOfSecurityType
import dev.shadoe.hotspotapi.helper.SoftApSecurityType.supportedSecurityTypes
import dev.shadoe.hotspotapi.helper.SoftApSpeedType.getNameOfSpeedType
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds

@Composable
fun HotspotEditScreen(modifier: Modifier = Modifier) {
    val navController = LocalNavController.current
    val focusManager = LocalFocusManager.current
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    val hotspotApi = LocalHotspotApiInstance.current!!
    val supportedSpeedTypes = hotspotApi.supportedSpeedTypes.collectAsState()
    val config = hotspotApi.config.collectAsState()

    var mutableConfig by remember(config.value) {
        println("triggering change")
        mutableStateOf(config.value)
    }

    Scaffold(
        topBar = {
            @OptIn(ExperimentalMaterial3Api::class)
            LargeTopAppBar(
                title = { Text(text = "Settings") },
                navigationIcon = {
                    IconButton(onClick = { navController?.navigateUp() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                            contentDescription = "Back",
                        )
                    }
                },
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
    ) { scaffoldPadding ->
        LazyColumn(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(scaffoldPadding)
                    .padding(horizontal = 16.dp)
                    .pointerInput(Unit) {
                        detectTapGestures(onTap = {
                            focusManager.clearFocus()
                        })
                    }.then(modifier),
        ) {
            item {
                Text(
                    text =
                        "Configure all the values below as per your" +
                            " liking :)",
                    modifier = Modifier.padding(8.dp),
                )
            }
            item {
                SSIDField(
                    ssid = mutableConfig.ssid ?: "",
                    onSSIDChange = {
                        mutableConfig = mutableConfig.copy(ssid = it)
                    },
                )
            }
            item {
                SecurityTypeField(
                    securityType = mutableConfig.securityType,
                    onSecurityTypeChange = {
                        mutableConfig = mutableConfig.copy(securityType = it)
                    },
                )
            }
            if (mutableConfig.securityType !=
                SoftApSecurityType.SECURITY_TYPE_OPEN
            ) {
                item {
                    PassphraseField(
                        passphrase = mutableConfig.passphrase,
                        onPassphraseChange = {
                            mutableConfig = mutableConfig.copy(passphrase = it)
                        },
                    )
                }
            }
            item {
                AutoShutdownField(
                    isAutoShutdownEnabled =
                        mutableConfig.isAutoShutdownEnabled,
                    onAutoShutdownChange = {
                        mutableConfig =
                            mutableConfig.copy(
                                isAutoShutdownEnabled = it,
                            )
                    },
                )
            }
            item {
                SpeedTypeField(
                    speedType = mutableConfig.speedType,
                    supportedSpeedTypes = supportedSpeedTypes.value,
                    onSpeedTypeChange = {
                        mutableConfig = mutableConfig.copy(speedType = it)
                    },
                )
            }
            item {
                Button(
                    onClick = onClick@{
                        if (mutableConfig.passphrase.isEmpty()) {
                            scope.launch {
                                snackbarHostState.showSnackbar(
                                    message = "Enter a password.",
                                    withDismissAction = true,
                                    duration = SnackbarDuration.Short,
                                )
                            }
                            return@onClick
                        }
                        scope
                            .launch {
                                setSoftApConfiguration(
                                    hotspotApi,
                                    mutableConfig,
                                )
                            }.invokeOnCompletion {
                                if (it == null) {
                                    navController?.navigateUp()
                                }
                            }
                    },
                ) {
                    Text(text = "Save")
                }
            }
        }
    }
}

private suspend fun setSoftApConfiguration(
    hotspotApi: HotspotApi,
    config: SoftApConfiguration,
) {
    hotspotApi.setSoftApConfiguration(config)
    val enabled = SoftApEnabledState.WIFI_AP_STATE_ENABLED
    val disabled = SoftApEnabledState.WIFI_AP_STATE_DISABLED
    val failed = SoftApEnabledState.WIFI_AP_STATE_FAILED
    if (hotspotApi.enabledState.value == failed) {
        hotspotApi.startHotspot(forceRestart = true)
        while (hotspotApi.enabledState.value != enabled) {
            delay(500.milliseconds)
        }
    } else if (hotspotApi.enabledState.value == enabled) {
        hotspotApi.stopHotspot()
        while (hotspotApi.enabledState.value != disabled) {
            delay(500.milliseconds)
        }
        hotspotApi.startHotspot()
        while (hotspotApi.enabledState.value != enabled) {
            delay(500.milliseconds)
        }
    }
}

@Composable
private fun SSIDField(
    ssid: String,
    onSSIDChange: (String) -> Unit,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(vertical = 8.dp),
    ) {
        Icon(
            imageVector = Icons.Rounded.Wifi,
            contentDescription = "SSID icon",
        )
        OutlinedTextField(
            value = ssid,
            onValueChange = { onSSIDChange(it) },
            singleLine = true,
            keyboardOptions =
                KeyboardOptions(
                    capitalization = KeyboardCapitalization.None,
                    autoCorrectEnabled = false,
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Next,
                ),
            modifier =
                Modifier
                    .padding(horizontal = 16.dp)
                    .fillMaxWidth(),
            label = { Text("SSID") },
        )
    }
}

@Composable
private fun SecurityTypeField(
    securityType: Int,
    onSecurityTypeChange: (Int) -> Unit,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(vertical = 8.dp),
    ) {
        Icon(
            imageVector = Icons.Rounded.WifiPassword,
            contentDescription = "Security Type icon",
        )
        Column(modifier = Modifier.padding(horizontal = 16.dp)) {
            Text(
                "Security Type",
                modifier = Modifier.padding(start = 8.dp),
                style = MaterialTheme.typography.titleMedium,
            )
            LazyRow {
                items(supportedSecurityTypes.size) {
                    FilterChip(
                        selected = securityType == supportedSecurityTypes[it],
                        onClick = {
                            onSecurityTypeChange(supportedSecurityTypes[it])
                        },
                        label = {
                            Text(
                                text =
                                    getNameOfSecurityType(
                                        supportedSecurityTypes[it],
                                    ),
                            )
                        },
                        modifier = Modifier.padding(horizontal = 2.dp),
                    )
                }
            }
        }
    }
}

@Composable
private fun PassphraseField(
    passphrase: String,
    onPassphraseChange: (String) -> Unit,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(vertical = 8.dp),
    ) {
        Icon(
            imageVector = Icons.Rounded.Password,
            contentDescription = "Passphrase icon",
        )
        OutlinedTextField(
            value = passphrase,
            onValueChange = { onPassphraseChange(it) },
            singleLine = true,
            keyboardOptions =
                KeyboardOptions(
                    capitalization = KeyboardCapitalization.None,
                    autoCorrectEnabled = false,
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Done,
                ),
            modifier =
                Modifier
                    .padding(horizontal = 16.dp)
                    .fillMaxWidth(),
            label = { Text("Passphrase") },
        )
    }
}

@Composable
private fun AutoShutdownField(
    isAutoShutdownEnabled: Boolean,
    onAutoShutdownChange: (Boolean) -> Unit,
) {
    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = Icons.Rounded.SettingsPower,
            contentDescription = "Auto Shutdown icon",
        )
        Column(
            modifier =
                Modifier
                    .weight(1f)
                    .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.Start,
        ) {
            Text(
                text = "Turn off hotspot automatically?",
                style = MaterialTheme.typography.titleLarge,
            )
            Text(
                text = "When no devices are connected.",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        Switch(
            checked = isAutoShutdownEnabled,
            onCheckedChange = { onAutoShutdownChange(it) },
        )
    }
}

@Composable
private fun SpeedTypeField(
    speedType: Int,
    supportedSpeedTypes: List<Int>,
    onSpeedTypeChange: (Int) -> Unit,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(vertical = 8.dp),
    ) {
        Icon(
            imageVector = Icons.Rounded.NetworkWifi,
            contentDescription = "Frequency band icon",
        )
        Column(modifier = Modifier.padding(horizontal = 16.dp)) {
            Text(
                "Frequency band",
                modifier = Modifier.padding(start = 8.dp),
                style = MaterialTheme.typography.titleMedium,
            )
            LazyRow {
                items(supportedSpeedTypes.size) {
                    FilterChip(
                        selected = speedType == supportedSpeedTypes[it],
                        onClick = {
                            onSpeedTypeChange(
                                supportedSpeedTypes[it],
                            )
                        },
                        label = {
                            Text(
                                text =
                                    getNameOfSpeedType(
                                        supportedSpeedTypes[it],
                                    ),
                            )
                        },
                        modifier = Modifier.padding(horizontal = 2.dp),
                    )
                }
            }
        }
    }
}
