package dev.shadoe.delta.shizuku

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import dev.shadoe.delta.R
import dev.shadoe.delta.shizuku.components.ShizukuNotConnected
import dev.shadoe.delta.shizuku.components.ShizukuNotInstalled
import dev.shadoe.delta.shizuku.components.ShizukuNotRunning

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShizukuSetupScreen(modifier: Modifier = Modifier) {
    val shizukuState = LocalShizukuState.current
    Scaffold(
        topBar = {
            if (shizukuState != NOT_READY && shizukuState != CONNECTED) {
                LargeTopAppBar(
                    title = { Text(stringResource(R.string.shizuku_setup)) },
                )
            }
        },
    ) {
        Column(modifier = Modifier.padding(it).then(modifier)) {
            when (LocalShizukuState.current) {
                NOT_AVAILABLE -> ShizukuNotInstalled()
                NOT_RUNNING -> ShizukuNotRunning()
                NOT_CONNECTED -> ShizukuNotConnected()
                else -> {}
            }
        }
    }
}
