package dev.shadoe.delta.screens

import android.content.Intent
import android.content.Intent.ACTION_VIEW
import android.net.Uri
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
import dev.shadoe.delta.shizuku.CONNECTED
import dev.shadoe.delta.shizuku.LocalShizukuState
import dev.shadoe.delta.shizuku.NOT_AVAILABLE
import dev.shadoe.delta.shizuku.NOT_READY
import dev.shadoe.delta.shizuku.NOT_RUNNING
import dev.shadoe.delta.shizuku.RUNNING
import dev.shadoe.delta.shizuku.ShizukuViewModel
import rikka.shizuku.Shizuku
import rikka.shizuku.ShizukuProvider

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShizukuSetup() {
    val shizukuState = LocalShizukuState.current
    Scaffold(
        topBar = {
            if (shizukuState != NOT_READY && shizukuState != CONNECTED) {
                LargeTopAppBar(title = { Text("Setup the app") })
            }
        }) {
        Column(modifier = Modifier.padding(it)) {
            when(LocalShizukuState.current) {
                NOT_AVAILABLE -> ShizukuNotInstalled()
                NOT_RUNNING -> ShizukuNotRunning()
                RUNNING -> ShizukuPrompt()
                else -> {}
            }
        }
    }
}

@Composable
private fun ShizukuNotInstalled() {
    val context = LocalContext.current
    Column {
        Text("Shizuku is not installed")
        Button(onClick = {
            context.startActivity(
                Intent(
                    ACTION_VIEW,
                    Uri.parse("https://github.com/RikkaApps/Shizuku/releases")
                )
            )
        }) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.AutoMirrored.Rounded.OpenInNew,
                    contentDescription = "Open"
                )
                Text(text = "GitHub", modifier = Modifier.padding(start = 8.dp))
            }
        }
        Button(onClick = {
            context.startActivity(Intent(ACTION_VIEW).apply {
                data = Uri.parse(
                    "https://play.google.com/store/apps/details?id=moe.shizuku.privileged.api"
                )
                `package` = "com.android.vending"
            })
        }) {
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
            Shizuku.requestPermission(ShizukuViewModel.PERM_REQ_CODE)
        }) {
            Text(text = "Grant access")
        }
    }
}