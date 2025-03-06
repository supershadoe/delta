package dev.shadoe.delta

import android.annotation.SuppressLint
import android.os.Build
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import dev.shadoe.delta.crash.CrashHandler
import dev.shadoe.delta.crash.CrashHandlerSetup
import dev.shadoe.delta.navigation.HotspotNavGraph
import dev.shadoe.delta.shizuku.ShizukuScope
import dev.shadoe.delta.shizuku.ShizukuSetupScreen
import dev.shadoe.delta.shizuku.ShizukuStates
import dev.shadoe.delta.typography.Typography

@Composable
fun App() {
  val colorScheme =
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
      dynamicDarkColorScheme(LocalContext.current)
    } else {
      darkColorScheme()
    }

  val context = LocalContext.current
  var isNotificationPermissionGranted by remember {
    mutableStateOf(
      !CrashHandler.shouldShowNotificationPermissionRequest(context)
    )
  }

  MaterialTheme(colorScheme = colorScheme, typography = Typography.value) {
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
