package dev.shadoe.delta.setup

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import dev.shadoe.delta.R
import dev.shadoe.delta.api.ShizukuStates.CONNECTED
import dev.shadoe.delta.api.ShizukuStates.NOT_AVAILABLE
import dev.shadoe.delta.api.ShizukuStates.NOT_CONNECTED
import dev.shadoe.delta.api.ShizukuStates.NOT_RUNNING
import dev.shadoe.delta.common.LocalNavController
import dev.shadoe.delta.common.Routes
import dev.shadoe.delta.setup.components.ShizukuConnected
import dev.shadoe.delta.setup.components.ShizukuNotConnected
import dev.shadoe.delta.setup.components.ShizukuNotInstalled
import dev.shadoe.delta.setup.components.ShizukuNotRunning

@Composable
fun ShizukuSetupScreen(vm: ShizukuSetupViewModel = viewModel()) {
  val navController = LocalNavController.current
  val state by vm.shizukuState.collectAsState()
  Scaffold(
    topBar = {
      @OptIn(ExperimentalMaterial3Api::class)
      LargeTopAppBar(title = { Text(stringResource(R.string.setup_title)) })
    }
  ) {
    Column(modifier = Modifier.padding(it)) {
      when (state) {
        NOT_AVAILABLE -> ShizukuNotInstalled()
        NOT_RUNNING -> ShizukuNotRunning()
        NOT_CONNECTED ->
          ShizukuNotConnected(onRequestPermission = { vm.requestPermission() })
        CONNECTED ->
          ShizukuConnected(
            continueAction = {
              navController?.navigate(
                route = Routes.Setup.CrashHandlerSetupScreen
              )
            }
          )
        else -> {}
      }
    }
  }
}
