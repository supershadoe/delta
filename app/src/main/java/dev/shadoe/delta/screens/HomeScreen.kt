package dev.shadoe.delta.screens

import android.net.IIntResultListener
import android.net.TetheringManager
import android.net.TetheringManager.TETHER_ERROR_NO_ERROR
import android.os.Build
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import dev.shadoe.delta.shizuku.HotspotApi
import java.util.concurrent.Executor

class HotspotViewModel: ViewModel() {
    private val intResultListener: IIntResultListener by lazy {
        object : IIntResultListener.Stub() {
            override fun onResult(resultCode: Int) {
                Executor { c -> c.run() }.execute {
                    if (resultCode == TETHER_ERROR_NO_ERROR) {
                        HotspotApi.startTetheringCallback.onTetheringStarted()
                    } else {
                        HotspotApi.startTetheringCallback.onTetheringFailed(
                            resultCode
                        )
                    }
                }
            }
        }
    }

    fun startHotspot(
        packageName: String,
        attributionTag: String,
    ): String {
        try {
            HotspotApi.wifiManager!!.registerSoftApCallback(HotspotApi.softApCallback)
            val tetheringRequest =
                TetheringManager.TetheringRequest.Builder(TetheringManager.TETHERING_WIFI)
                    .setSoftApConfiguration(HotspotApi.wifiManager!!.softApConfiguration)
                    .build()
            HotspotApi.tetheringManager!!.startTethering(
                tetheringRequest.parcel,
                packageName,
                attributionTag,
                intResultListener,
            )
            return "Success"
        } catch (e: Exception) {
            return e.stackTraceToString()
        }
    }

    fun stopHotspot(
        packageName: String,
        attributionTag: String,
    ): String {
        try {
            HotspotApi.tetheringManager!!.stopTethering(
                TetheringManager.TETHERING_WIFI,
                packageName,
                attributionTag,
                intResultListener
            )
            HotspotApi.wifiManager!!.unregisterSoftApCallback(
                HotspotApi.softApCallback
            )
            return "Success"
        } catch (e: Exception) {
            return e.stackTraceToString()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun HomeScreen(
    viewModel: HotspotViewModel = viewModel()
) {
    val errText = remember { mutableStateOf("") }
    val ssid = remember { mutableStateOf(HotspotApi.ssid ?: "") }
    val password = remember {
        mutableStateOf(HotspotApi.passphrase ?: "")
    }
    val areDevicesAvailable = remember { mutableStateOf(false) }
    val isHotspotRunning = remember { mutableStateOf(false) }

    val packageName = LocalContext.current.packageName
    val attributionTag =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) LocalContext.current.attributionTag
        else {
            ""
        }

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
            if (!isHotspotRunning.value)
            Button(onClick = {
                val res = viewModel.startHotspot(packageName, attributionTag.toString())
                if (res == "Success") {
                    isHotspotRunning.value = true
                } else {
                    errText.value = res
                }
            }) {
                Text(text = "Start hotspot")
            }
            if (isHotspotRunning.value)
            Button(onClick = {
                val res = viewModel.stopHotspot(packageName, attributionTag.toString())
                if (res == "Success") {
                    isHotspotRunning.value = false
                } else {
                    errText.value = res
                }
            }) {
                Text(text = "Stop hotspot")
            }
            Column(modifier = Modifier.padding(horizontal = 24.dp)) {
                if (!areDevicesAvailable.value) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .fillMaxHeight(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(text = "No devices connected")
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
                        items(10) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.padding(horizontal = 8.dp)) {
                                    Text(text = "some-device")
                                    Text(text = "00:01:00")
                                }
                                Button(onClick = {}) {
                                    Text(text = "BLOCK")
                                }
                            }
                        }
                    }
                }
            }
        }

        if (errText.value.isNotEmpty()) {
            AlertDialog(
                onDismissRequest = { errText.value = "" },
                title = { Text(text = "Error occurred") },
                text = {
                    LazyColumn {
                        item {
                            Text(
                                text = errText.value,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }
                },
                confirmButton = {
                    TextButton(onClick = { errText.value = "" }) {
                        Text(text = "Dismiss")
                    }
                },
            )
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
