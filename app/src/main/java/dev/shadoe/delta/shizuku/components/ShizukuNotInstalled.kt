package dev.shadoe.delta.shizuku.components

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
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp

@Composable
internal fun ShizukuNotInstalled() {
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
