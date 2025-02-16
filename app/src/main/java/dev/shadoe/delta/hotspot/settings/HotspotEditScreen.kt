package dev.shadoe.delta.hotspot.settings

import android.net.wifi.SoftApConfiguration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.shadoe.delta.hotspot.LocalHotspotApiInstance
import dev.shadoe.delta.hotspot.navigation.LocalNavController
import dev.shadoe.hotspotapi.WifiApEnabledStates
import kotlinx.coroutines.delay

@Composable
fun HotspotEditScreen() {
    val navController = LocalNavController.current

    val hotspotApi = LocalHotspotApiInstance.current!!
    val enabledState = hotspotApi.enabledState.collectAsState()
    val ssid = hotspotApi.ssid.collectAsState(null)
    val password = hotspotApi.passphrase.collectAsState(null)
    val securityType =
        hotspotApi.securityType.collectAsState(SoftApConfiguration.SECURITY_TYPE_OPEN)

    val allowEdits = remember { mutableStateOf(true) }
    val updateCounter = remember { mutableIntStateOf(0) }

    LaunchedEffect(updateCounter.intValue) {
        // TODO: consider using a button for applying all changes at once
        // instead of this bs
        if (updateCounter.intValue == 0) return@LaunchedEffect
        if (enabledState.value == WifiApEnabledStates.WIFI_AP_STATE_ENABLED) {
            allowEdits.value = false
            hotspotApi.stopHotspot()
            while (enabledState.value != WifiApEnabledStates.WIFI_AP_STATE_DISABLED) {
                delay(500)
            }
            hotspotApi.startHotspot()
            while (enabledState.value != WifiApEnabledStates.WIFI_AP_STATE_ENABLED) {
                delay(500)
            }
            allowEdits.value = true
        }
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
        }) { scaffoldPadding ->
        if (allowEdits.value) {
            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .padding(scaffoldPadding)
            ) {
                EditSsidField(
                    value = ssid.value ?: "",
                    onSave = {
                        hotspotApi.setSsid(it)
                        updateCounter.intValue++
                    },
                )
                if (securityType.value != SoftApConfiguration.SECURITY_TYPE_OPEN) {
                    EditPassphraseField(
                        value = password.value ?: "",
                        onSave = {
                            hotspotApi.setPassphrase(it)
                            updateCounter.intValue++
                        },
                    )
                }
                EditSecurityType(
                    value = securityType.value,
                    onSave = {
                        hotspotApi.setSecurityType(it)
                        updateCounter.intValue++
                    },
                )
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(scaffoldPadding),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                CircularProgressIndicator()
                Text(
                    text = "Updating settings…",
                    modifier = Modifier.padding(top = 8.dp),
                )
            }
        }
    }
}