package dev.shadoe.delta.hotspot

import android.net.wifi.SoftApConfiguration
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import dev.shadoe.delta.hotspot.navigation.LocalNavController
import dev.shadoe.hotspotapi.SoftApSecurityType
import dev.shadoe.hotspotapi.WifiApEnabledStates
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds

private fun getNameOfSecurityType(@SoftApSecurityType.SecurityType securityType: Int): String {
    return when (securityType) {
        SoftApSecurityType.SECURITY_TYPE_OPEN -> "None"
        SoftApSecurityType.SECURITY_TYPE_WPA2_PSK -> "WPA2-Personal"
        SoftApSecurityType.SECURITY_TYPE_WPA3_SAE -> "WPA3-Personal"
        SoftApSecurityType.SECURITY_TYPE_WPA3_SAE_TRANSITION -> "WPA2/WPA3-Personal"
        else -> "Not supported"
    }
}

private val supportedSecurityTypes = intArrayOf(
    SoftApConfiguration.SECURITY_TYPE_WPA3_SAE,
    SoftApConfiguration.SECURITY_TYPE_WPA3_SAE_TRANSITION,
    SoftApConfiguration.SECURITY_TYPE_WPA2_PSK,
    SoftApConfiguration.SECURITY_TYPE_OPEN,
)

@Composable
fun HotspotEditScreen() {
    val navController = LocalNavController.current
    val focusManager = LocalFocusManager.current
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    val hotspotApi = LocalHotspotApiInstance.current!!

    val ssid = hotspotApi.ssid.collectAsState(null)
    val passphrase = hotspotApi.passphrase.collectAsState("")
    val securityType =
        hotspotApi.securityType.collectAsState(SoftApConfiguration.SECURITY_TYPE_OPEN)
    val isAutoShutdownEnabled =
        hotspotApi.isAutoShutdownEnabled.collectAsState(false)
    val ssidField = remember(ssid.value) { mutableStateOf(ssid.value) }
    val passphraseField =
        remember(passphrase.value) { mutableStateOf(passphrase.value) }
    val securityTypeField =
        remember(securityType.value) { mutableIntStateOf(securityType.value) }
    val autoShutdownField = remember(isAutoShutdownEnabled.value) {
        mutableStateOf(isAutoShutdownEnabled.value)
    }

    LaunchedEffect(securityType.value) {
        hotspotApi.queryLastUsedPassphraseSinceBoot()
    }

    Scaffold(
        topBar = {
            @OptIn(ExperimentalMaterial3Api::class) LargeTopAppBar(
                title = { Text(text = "Settings") },
                navigationIcon = {
                    IconButton(onClick = { navController?.navigateUp() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
    ) { scaffoldPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(scaffoldPadding)
                .padding(horizontal = 16.dp)
                .pointerInput(Unit) {
                    detectTapGestures(onTap = {
                        focusManager.clearFocus()
                    })
                },
        ) {
            Text(
                text = "Configure all the values below as per your liking :)",
                modifier = Modifier.padding(8.dp)
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(vertical = 8.dp)
            ) {
                Icon(
                    imageVector = Icons.Rounded.Wifi,
                    contentDescription = "SSID icon"
                )
                OutlinedTextField(
                    value = ssidField.value ?: "",
                    onValueChange = { ssidField.value = it },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.None,
                        autoCorrectEnabled = false,
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Next,
                    ),
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .fillMaxWidth(),
                    label = { Text("SSID") },
                )
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(vertical = 8.dp)
            ) {
                Icon(
                    imageVector = Icons.Rounded.WifiPassword,
                    contentDescription = "Security Type icon"
                )
                Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                    Text(
                        "Security Type",
                        modifier = Modifier.padding(start = 8.dp),
                        style = MaterialTheme.typography.titleMedium
                    )
                    LazyRow {
                        items(supportedSecurityTypes.size) {
                            FilterChip(
                                selected = securityTypeField.intValue == supportedSecurityTypes[it],
                                onClick = {
                                    securityTypeField.intValue =
                                        supportedSecurityTypes[it]
                                },
                                label = {
                                    Text(
                                        text = getNameOfSecurityType(
                                            supportedSecurityTypes[it]
                                        )
                                    )
                                },
                                modifier = Modifier.padding(horizontal = 2.dp)
                            )
                        }
                    }
                }
            }
            if (securityTypeField.intValue != SoftApConfiguration.SECURITY_TYPE_OPEN) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(vertical = 8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Password,
                        contentDescription = "Passphrase icon"
                    )
                    OutlinedTextField(
                        value = passphraseField.value,
                        onValueChange = { passphraseField.value = it },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(
                            capitalization = KeyboardCapitalization.None,
                            autoCorrectEnabled = false,
                            keyboardType = KeyboardType.Password,
                            imeAction = ImeAction.Done,
                        ),
                        modifier = Modifier
                            .padding(horizontal = 16.dp)
                            .fillMaxWidth(),
                        label = { Text("Passphrase") },
                    )
                }
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Rounded.SettingsPower,
                    contentDescription = "Auto Shutdown icon"
                )
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 16.dp),
                    horizontalAlignment = Alignment.Start
                ) {
                    Text(
                        text = "Turn off hotspot automatically?",
                        style = MaterialTheme.typography.titleLarge
                    )
                    Text(
                        text = "When no devices are connected.",
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Switch(
                    checked = autoShutdownField.value,
                    onCheckedChange = { autoShutdownField.value = it },
                )
            }

            Button(onClick = onClick@{
                var passphrase: String? = passphraseField.value
                when {
                    securityTypeField.intValue == SoftApSecurityType.SECURITY_TYPE_OPEN -> {
                        passphrase = null
                    }

                    passphraseField.value.isEmpty() -> {
                        scope.launch {
                            snackbarHostState.showSnackbar(
                                message = "Enter a password.",
                                withDismissAction = true,
                                duration = SnackbarDuration.Short,
                            )
                        }
                        return@onClick
                    }
                }
                scope.launch {
                    hotspotApi.setSsid(ssidField.value)
                    hotspotApi.setPassphrase(
                        passphrase, securityTypeField.intValue
                    )
                    hotspotApi.setAutoShutdownState(autoShutdownField.value)
                    if (hotspotApi.enabledState.value == WifiApEnabledStates.WIFI_AP_STATE_ENABLED) {
                        hotspotApi.stopHotspot()
                        while (hotspotApi.enabledState.value != WifiApEnabledStates.WIFI_AP_STATE_DISABLED) {
                            delay(500.milliseconds)
                        }
                        hotspotApi.startHotspot()
                        while (hotspotApi.enabledState.value != WifiApEnabledStates.WIFI_AP_STATE_ENABLED) {
                            delay(500.milliseconds)
                        }
                    }
                }
                    .invokeOnCompletion { if (it == null) navController?.navigateUp() }
            }) {
                Text(text = "Save")
            }
        }
    }
}