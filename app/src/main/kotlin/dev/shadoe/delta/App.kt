package dev.shadoe.delta

import android.annotation.SuppressLint
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import dev.shadoe.delta.crash.CrashHandlerSetup
import dev.shadoe.delta.crash.CrashHandlerUtils
import dev.shadoe.delta.design.AppTheme
import dev.shadoe.delta.navigation.HotspotNavGraph
import dev.shadoe.delta.shizuku.ShizukuScope
import dev.shadoe.delta.shizuku.ShizukuSetupScreen
import dev.shadoe.delta.shizuku.ShizukuStates

@Composable
fun App() {
  val context = LocalContext.current
  var isNotificationPermissionGranted by remember {
    mutableStateOf(
      !CrashHandlerUtils.shouldShowNotificationPermissionRequest(context)
    )
  }

  AppTheme {
    if (!isNotificationPermissionGranted) {
      @SuppressLint("NewApi")
      CrashHandlerSetup(
        onPermissionGranted = { isNotificationPermissionGranted = true }
      )
    } else {
      ShizukuScope { vm ->
        val state by vm.shizukuState.collectAsState()
        if (state == ShizukuStates.CONNECTED) {
          HotspotNavGraph()
        } else {
          ShizukuSetupScreen(
            state = state,
            onRequestPermission = { vm.requestPermission() },
          )
        }
      }
    }
  }
}
