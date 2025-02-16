package dev.shadoe.delta.hotspot.settings

import android.net.wifi.SoftApConfiguration
import android.net.wifi.SoftApConfiguration.SecurityType
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Password
import androidx.compose.material.icons.rounded.Wifi
import androidx.compose.material.icons.rounded.WifiPassword
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import dev.shadoe.delta.hotspot.LocalHotspotApiInstance
import dev.shadoe.delta.hotspot.navigation.LocalNavController
import dev.shadoe.hotspotapi.WifiApEnabledStates
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds

private fun getNameOfSecurityType(@SecurityType securityType: Int): String {
    return when (securityType) {
        SoftApConfiguration.SECURITY_TYPE_OPEN -> "None"
        SoftApConfiguration.SECURITY_TYPE_WPA2_PSK -> "WPA2-Personal"
        SoftApConfiguration.SECURITY_TYPE_WPA3_SAE -> "WPA3-Personal"
        SoftApConfiguration.SECURITY_TYPE_WPA3_SAE_TRANSITION -> "WPA2/WPA3-Personal"
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
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    val hotspotApi = LocalHotspotApiInstance.current!!

    val ssid = hotspotApi.ssid.collectAsState(null)
    val passphrase = hotspotApi.passphrase.collectAsState(null)
    val securityType =
        hotspotApi.securityType.collectAsState(SoftApConfiguration.SECURITY_TYPE_OPEN)
    val ssidField = remember(ssid.value) { mutableStateOf(ssid.value) }
    val passphraseField =
        remember(passphrase.value) { mutableStateOf(passphrase.value) }
    val securityTypeField =
        remember(securityType.value) { mutableIntStateOf(securityType.value) }

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
                .padding(horizontal = 16.dp),
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Rounded.Wifi,
                    contentDescription = "SSID icon"
                )
                Text(
                    "SSID",
                    modifier = Modifier.padding(start = 8.dp),
                    style = MaterialTheme.typography.titleLarge
                )
            }
            TextField(
                value = ssidField.value ?: "",
                onValueChange = { ssidField.value = it },
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.None,
                    autoCorrectEnabled = false,
                    keyboardType = KeyboardType.Text,
                ),
                modifier = Modifier.padding(vertical = 16.dp),
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Rounded.WifiPassword,
                    contentDescription = "Security Type icon"
                )
                Text(
                    "Security",
                    modifier = Modifier.padding(start = 8.dp),
                    style = MaterialTheme.typography.titleLarge
                )
            }
            @OptIn(ExperimentalLayoutApi::class) FlowRow(
                modifier = Modifier.padding(vertical = 16.dp),
            ) {
                supportedSecurityTypes.forEach {
                    FilterChip(
                        selected = securityTypeField.intValue == it,
                        onClick = {
                            securityTypeField.intValue = it
                        },
                        label = {
                            Text(text = getNameOfSecurityType(it))
                        },
                        modifier = Modifier.padding(horizontal = 8.dp),
                    )
                }
            }

            if (securityTypeField.intValue != SoftApConfiguration.SECURITY_TYPE_OPEN) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Rounded.Password,
                        contentDescription = "SSID icon"
                    )
                    Text(
                        "Passphrase",
                        modifier = Modifier.padding(start = 8.dp),
                        style = MaterialTheme.typography.titleLarge
                    )
                }
                TextField(
                    value = passphraseField.value ?: "",
                    onValueChange = { passphraseField.value = it },
                    keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.None,
                        autoCorrectEnabled = false,
                        keyboardType = KeyboardType.Password,
                    ),
                    modifier = Modifier.padding(vertical = 16.dp),
                )
            }

            Button(onClick = onClick@{
                var passphrase = passphraseField.value
                if (securityTypeField.intValue == SoftApConfiguration.SECURITY_TYPE_OPEN) {
                    passphrase = null
                } else if ((passphraseField.value ?: "").isEmpty()) {
                    scope.launch {
                        snackbarHostState.showSnackbar(
                            message = "Enter a password.",
                            withDismissAction = true,
                            duration = SnackbarDuration.Short,
                        )
                    }
                    return@onClick
                }
                scope.launch {
                    hotspotApi.setSsid(ssidField.value)
                    hotspotApi.setPassphrase(
                        passphrase, securityTypeField.intValue
                    )
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
                    navController?.navigateUp()
                }
            }) {
                Text(text = "Save")
            }
        }
    }
}