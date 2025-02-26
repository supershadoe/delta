package dev.shadoe.delta.shizuku.components

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import dev.shadoe.delta.R
import rikka.shizuku.ShizukuProvider

@Composable
internal fun ShizukuNotRunning() {
    val context = LocalContext.current
    Column {
        Text(stringResource(R.string.shizuku_not_running))
        Button(onClick = {
            with(context) {
                startActivity(
                    packageManager.getLaunchIntentForPackage(
                        ShizukuProvider.MANAGER_APPLICATION_ID,
                    ),
                )
            }
        }) {
            Text(text = stringResource(R.string.shizuku_start))
        }
    }
}
