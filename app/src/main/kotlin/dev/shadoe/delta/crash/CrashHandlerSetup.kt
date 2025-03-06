package dev.shadoe.delta.crash

import android.Manifest
import android.os.Build
import androidx.activity.compose.LocalActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun CrashHandlerSetup(
  modifier: Modifier = Modifier,
  onPermissionGranted: () -> Unit,
) {
  val activity = LocalActivity.current!!
  val launcher =
    rememberLauncherForActivityResult(
      ActivityResultContracts.RequestPermission()
    ) {
      if (it) onPermissionGranted()
    }
  Scaffold(
    topBar = {
      @OptIn(ExperimentalMaterial3Api::class)
      LargeTopAppBar(title = { Text(text = "Setup") })
    }
  ) {
    Column(Modifier.padding(it).padding(24.dp)) {
      Text(
        "We need notification permission to show you crash logs if any crashes occur."
      )
      Button(
        onClick = {
          if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU)
            return@Button
          launcher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
      ) {
        Text(text = "Give permission")
      }
    }
  }
}
