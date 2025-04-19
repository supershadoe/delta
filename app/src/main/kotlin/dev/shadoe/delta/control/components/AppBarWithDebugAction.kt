package dev.shadoe.delta.control.components

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import dev.shadoe.delta.R

@Composable
fun AppBarWithDebugAction(onNavigateToDebug: () -> Unit) {
  val context = LocalContext.current
  var debugTaps by remember { mutableIntStateOf(0) }
  val stringTriggeredDebugScreen =
    stringResource(R.string.triggered_debug_screen)
  Text(
    text = stringResource(R.string.app_name),
    modifier =
      Modifier.clickable {
        debugTaps += 1
        if (debugTaps % 5 == 0) {
          onNavigateToDebug()
          Toast.makeText(
              context,
              stringTriggeredDebugScreen,
              Toast.LENGTH_SHORT,
            )
            .show()
        }
      },
  )
}
