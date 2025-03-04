package dev.shadoe.delta

import android.os.Build
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import dev.shadoe.delta.navigation.HotspotNavGraph
import dev.shadoe.delta.presentation.shizuku.ShizukuStates
import dev.shadoe.delta.shizuku.ShizukuScope
import dev.shadoe.delta.shizuku.ShizukuSetupScreen
import dev.shadoe.delta.typography.Typography

@Composable
fun App() {
  val colorScheme =
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
      dynamicDarkColorScheme(LocalContext.current)
    } else {
      darkColorScheme()
    }

  MaterialTheme(colorScheme = colorScheme, typography = Typography.value) {
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
