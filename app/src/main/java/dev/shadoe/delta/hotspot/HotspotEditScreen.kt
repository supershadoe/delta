package dev.shadoe.delta.hotspot

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Password
import androidx.compose.material.icons.rounded.Wifi
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import dev.shadoe.delta.hotspot.navigation.LocalNavController
import dev.shadoe.hotspotapi.WifiApEnabledStates
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HotspotEditScreen() {
    val navController = LocalNavController.current

    val hotspotApi = LocalHotspotApiInstance.current!!
    val enabledState = hotspotApi.enabledState.collectAsState()
    val ssid = hotspotApi.ssid.collectAsState(null)
    val password = hotspotApi.passphrase.collectAsState(null)

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
            LargeTopAppBar(
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
                EditWidget(
                    onClickLabel = "Edit SSID",
                    icon = Icons.Rounded.Wifi,
                    text = "SSID",
                    value = ssid.value ?: "",
                    onUpdate = { updateCounter.intValue++ },
                    onSave = { hotspotApi.setSsid(it) },
                )
                EditWidget(
                    onClickLabel = "Edit Password",
                    icon = Icons.Rounded.Password,
                    text = "Password",
                    maskValue = true,
                    value = password.value ?: "",
                    onUpdate = { updateCounter.intValue++ },
                    onSave = { hotspotApi.setPassphrase(it) },
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

@Composable
private fun EditWidget(
    onClickLabel: String,
    icon: ImageVector,
    text: String,
    value: String,
    onUpdate: () -> Unit,
    maskValue: Boolean = false,
    onSave: (String) -> Unit = {},
) {
    val isEditing = remember { mutableStateOf(false) }
    val textFieldState = remember(value) { mutableStateOf(value) }
    val onDone = {
        runBlocking {
            launch(Dispatchers.IO) {
                onSave(textFieldState.value)
            }
        }
        isEditing.value = false
        onUpdate()
    }
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
                Text(text = if (maskValue) "********" else textFieldState.value)
            }
        }
    }
    if (isEditing.value) AlertDialog(
        onDismissRequest = {
            textFieldState.value = value
            isEditing.value = false
        },
        title = { Text(text = "Edit $text") },
        text = {
            TextField(
                value = textFieldState.value,
                onValueChange = { textFieldState.value = it },
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.None,
                    autoCorrectEnabled = false,
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Done,
                ),
                keyboardActions = KeyboardActions(onDone = { onDone() }),
            )
        },
        confirmButton = {
            TextButton(onClick = onDone) {
                Text(text = "Save")
            }
        },
    )
}