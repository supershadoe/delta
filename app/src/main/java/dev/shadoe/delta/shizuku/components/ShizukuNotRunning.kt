package dev.shadoe.delta.shizuku.components

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import rikka.shizuku.ShizukuProvider

@Composable
internal fun ShizukuNotRunning() {
    val context = LocalContext.current
    Column {
        Text("Shizuku is not running")
        Button(onClick = {
            with(context) {
                startActivity(
                    packageManager.getLaunchIntentForPackage(
                        ShizukuProvider.MANAGER_APPLICATION_ID,
                    ),
                )
            }
        }) {
            Text(text = "Start Shizuku")
        }
    }
}
