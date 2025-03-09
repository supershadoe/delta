package dev.shadoe.delta.crash

import android.Manifest
import android.os.Build
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import dev.shadoe.delta.R

@Composable
fun CrashHandlerSetup(
  modifier: Modifier = Modifier,
  onPermissionGranted: () -> Unit,
) {
  val launcher =
    rememberLauncherForActivityResult(
      ActivityResultContracts.RequestPermission()
    ) {
      if (it) onPermissionGranted()
    }
  Scaffold(
    topBar = {
      @OptIn(ExperimentalMaterial3Api::class)
      LargeTopAppBar(
        title = { Text(text = stringResource(R.string.setup_title)) }
      )
    }
  ) {
    Column(Modifier.padding(it).padding(24.dp).then(modifier)) {
      Text(stringResource(R.string.crash_report_setup_desc))
      Button(
        onClick = {
          if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU)
            return@Button
          launcher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
      ) {
        Text(text = stringResource(R.string.crash_report_setup_grant_perm))
      }
    }
  }
}
