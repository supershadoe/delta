package dev.shadoe.delta.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.OpenInNew
import androidx.compose.material.icons.rounded.InstallMobile
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import dev.shadoe.delta.navigation.Nav.LocalNavController
import dev.shadoe.delta.navigation.Routes
import dev.shadoe.delta.shizuku.LocalShizukuConnected
import dev.shadoe.delta.shizuku.LocalShizukuRunning
import dev.shadoe.delta.shizuku.LocalSuiAvailable
import dev.shadoe.delta.shizuku.ShizukuUtils
import rikka.shizuku.ShizukuProvider

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShizukuSetup1() {
    Scaffold(
        topBar = {
            LargeTopAppBar(title = { Text("Setup the app") })
        }) {
        Column(modifier = Modifier.padding(it)) {
            ShizukuNotInstalled()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShizukuSetup() {
    val navController = LocalNavController.current
    val isShizukuInstalled =
        LocalSuiAvailable.current || ShizukuUtils.isShizukuInstalled(
            LocalContext.current.packageManager
        )
    Scaffold(
        topBar = {
            LargeTopAppBar(title = { Text("Setup the app") })
        }) {
        Column(modifier = Modifier.padding(it)) {
            when {
                !isShizukuInstalled -> ShizukuNotInstalled()

                LocalShizukuRunning.current -> if (LocalShizukuConnected.current) {
                    ShizukuConnected {
                        navController?.navigate(Routes.HomeScreen) {
                            popUpTo(Routes.ShizukuSetup) {
                                inclusive = true
                            }
                        }
                    }
                } else {
                    ShizukuPrompt()
                }

                else -> ShizukuNotRunning()
            }
        }
    }
}

@Composable
private fun ShizukuConnected(onClick: () -> Unit) {
    Column {
        Text("Connected to Shizuku, tap on the button to proceed with the next steps.")
        Button(onClick = onClick) {
            Text(text = "Finish setup")
        }
    }
}

@Composable
private fun ShizukuNotInstalled() {
    Column {
        Text("Shizuku is not installed")
        Button(onClick = {}) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.AutoMirrored.Rounded.OpenInNew,
                    contentDescription = "Open"
                )
                Text(text = "GitHub", modifier = Modifier.padding(start = 8.dp))
            }
        }
        Button(onClick = {}) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Rounded.InstallMobile,
                    contentDescription = "Install from"
                )
                Text(
                    text = "Play store",
                    modifier = Modifier.padding(start = 8.dp)
                )
            }
        }
    }
}

@Composable
private fun ShizukuNotRunning() {
    val context = LocalContext.current
    Column {
        Text("Shizuku is not running")
        Button(onClick = {
            with(context) {
                startActivity(
                    packageManager.getLaunchIntentForPackage(
                        ShizukuProvider.MANAGER_APPLICATION_ID
                    )
                )
            }
        }) {
            Text(text = "Start Shizuku")
        }
    }
}

@Composable
private fun ShizukuPrompt() {
    Column {
        Text("This app uses system APIs that are not generally accessible from the Android SDK and thus, requires Shizuku to get access to Hotspot API.")
        Button(onClick = {
            if (!ShizukuUtils.checkShizukuPerm()) {
                ShizukuUtils.getPerm()
            }
        }) {
            Text(text = "Grant access")
        }
    }
}