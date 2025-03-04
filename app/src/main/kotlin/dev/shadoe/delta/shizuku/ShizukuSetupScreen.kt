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
import dev.shadoe.delta.shizuku.ShizukuStates.CONNECTED
import dev.shadoe.delta.shizuku.ShizukuStates.NOT_AVAILABLE
import dev.shadoe.delta.shizuku.ShizukuStates.NOT_CONNECTED
import dev.shadoe.delta.shizuku.ShizukuStates.NOT_READY
import dev.shadoe.delta.shizuku.ShizukuStates.NOT_RUNNING
import dev.shadoe.delta.shizuku.ShizukuStates.ShizukuStateType
import dev.shadoe.delta.shizuku.components.ShizukuNotConnected
import dev.shadoe.delta.shizuku.components.ShizukuNotInstalled
import dev.shadoe.delta.shizuku.components.ShizukuNotRunning

@Composable
fun ShizukuSetupScreen(
  @ShizukuStateType state: Int,
  onRequestPermission: () -> Unit,
  modifier: Modifier = Modifier,
) {
  Scaffold(
    topBar = {
      if (state != NOT_READY && state != CONNECTED) {
        @OptIn(ExperimentalMaterial3Api::class)
        LargeTopAppBar(title = { Text(stringResource(R.string.shizuku_setup)) })
      }
    }
  ) {
    Column(modifier = Modifier.padding(it).then(modifier)) {
      when (state) {
        NOT_AVAILABLE -> ShizukuNotInstalled()
        NOT_RUNNING -> ShizukuNotRunning()
        NOT_CONNECTED -> ShizukuNotConnected(onRequestPermission)
        else -> {}
      }
    }
  }
}
