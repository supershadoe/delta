package dev.shadoe.delta.shizuku.components

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import dev.shadoe.delta.shizuku.ShizukuViewModel
import rikka.shizuku.Shizuku

@Composable
internal fun ShizukuNotConnected() {
    Column {
        Text("This app uses system APIs that are not generally accessible from the Android SDK and thus, requires Shizuku to get access to Hotspot API.")
        Button(onClick = {
            Shizuku.requestPermission(ShizukuViewModel.PERM_REQ_CODE)
        }) {
            Text(text = "Grant access")
        }
    }
}