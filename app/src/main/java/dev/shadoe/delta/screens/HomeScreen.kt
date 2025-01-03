package dev.shadoe.delta.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Password
import androidx.compose.material.icons.rounded.Wifi
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.shadoe.delta.shizuku.LocalShizukuConnected
import dev.shadoe.delta.shizuku.LocalShizukuRunning
import dev.shadoe.delta.shizuku.SystemApiAccess

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun HomeScreen() {
    val ssid = remember { mutableStateOf(SystemApiAccess.ssid ?: "") }
    val password = remember {
        mutableStateOf(
            SystemApiAccess.passphrase ?: "Password not available"
        )
    }
    Scaffold(
        topBar = {
            LargeTopAppBar(
                title = { Text(text = "Delta") })
        }) {
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth()
                .padding(it)
                .padding(horizontal = 24.dp)
        ) {
            Text(text = "Shizuku Running? ${LocalShizukuRunning.current}")
            Text(text = "Shizuku Connected? ${LocalShizukuConnected.current}")
            Row(
                modifier = Modifier.padding(vertical = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Rounded.Wifi,
                    contentDescription = "Edit"
                )
                Column(modifier = Modifier.padding(start = 16.dp)) {
                    Text(text = "SSID")
                    Text(text = ssid.value)
                }
            }
            Row(
                modifier = Modifier.padding(vertical = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Rounded.Password,
                    contentDescription = "Edit"
                )
                Column(modifier = Modifier.padding(start = 16.dp)) {
                    Text(text = "Password")
                    Text(text = password.value)
                }
            }
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(text = "No devices connected")
            }
//            Text(
//                text = "Connected Devices",
//                color = MaterialTheme.colorScheme.onSurface
//            )
//            LazyColumn(
//                contentPadding = PaddingValues(vertical = 16.dp),
//                verticalArrangement = Arrangement.spacedBy(16.dp),
//            ) {
//                items(10) {
//                    Row(
//                        modifier = Modifier.fillMaxWidth(),
//                        horizontalArrangement = Arrangement.SpaceBetween,
//                        verticalAlignment = Alignment.CenterVertically
//                    ) {
//                        Column(modifier = Modifier.padding(horizontal = 8.dp)) {
//                            Text(text = "some-device")
//                            Text(text = "00:01:00")
//                        }
//                        Button(onClick = {}) {
//                            Text(text = "BLOCK")
//                        }
//                    }
//                }
//            }
        }
    }
}
