package dev.shadoe.delta.crash

import android.content.Intent
import android.content.Intent.ACTION_SEND
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.OpenInNew
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.app.NotificationManagerCompat
import dev.shadoe.delta.R
import dev.shadoe.delta.typography.Typography

class CrashHandlerActivity : ComponentActivity() {
  companion object {
    const val EXTRA_CRASH_INFO = "dev.shadoe.delta.crashInfo"
  }

  val logHeader =
    """
    === beginning of metadata
    Manufacturer: ${Build.MANUFACTURER} (${Build.BRAND})
    Model: ${Build.MODEL} (${Build.DEVICE})
    OS: Android ${Build.VERSION.RELEASE_OR_CODENAME} (${Build.VERSION.SDK_INT})
    === end of metadata

    === beginning of crash log

    """
      .trimIndent()

  val logTrailer =
    """

    === end of crash log
  """
      .trimIndent()

  private fun formatLog(crashLog: String) = "$logHeader$crashLog$logTrailer"

  override fun onCreate(savedInstanceState: Bundle?) {
    enableEdgeToEdge()
    super.onCreate(savedInstanceState)
    NotificationManagerCompat.from(this).cancel(CrashHandler.CRASH_NOTIF_ID)
    setContent {
      val colorScheme =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
          dynamicDarkColorScheme(LocalContext.current)
        } else {
          darkColorScheme()
        }
      val crashLog = remember {
        intent.getStringExtra(EXTRA_CRASH_INFO)?.let { formatLog(it) }
      }

      MaterialTheme(colorScheme = colorScheme, typography = Typography.value) {
        Scaffold(
          topBar = {
            @OptIn(ExperimentalMaterial3Api::class)
            TopAppBar(
              title = {
                Text(text = stringResource(R.string.crash_report_title))
              }
            )
          }
        ) {
          Column(
            Modifier.padding(it).padding(vertical = 8.dp, horizontal = 24.dp)
          ) {
            Text(text = stringResource(R.string.crash_report_desc))
            if (crashLog == null) {
              Text(text = stringResource(R.string.crash_report_no_log))
            } else {
              Box(
                modifier =
                  Modifier.padding(vertical = 8.dp)
                    .fillMaxHeight(0.8f)
                    .border(1.dp, Color.Transparent, RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.primaryContainer)
              ) {
                Text(
                  text = crashLog,
                  modifier =
                    Modifier.verticalScroll(rememberScrollState()).padding(8.dp),
                )
              }
              Button(
                onClick = {
                  val intent =
                    Intent(ACTION_SEND).apply {
                      putExtra(Intent.EXTRA_TEXT, crashLog)
                      type = "text/plain"
                    }
                  val shareIntent = Intent.createChooser(intent, null)
                  startActivity(shareIntent)
                }
              ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                  Icon(
                    imageVector = Icons.AutoMirrored.Rounded.OpenInNew,
                    contentDescription = stringResource(R.string.open_icon),
                  )
                  Text(
                    text = stringResource(R.string.crash_notif_action),
                    modifier = Modifier.padding(start = 8.dp),
                  )
                }
              }
            }
          }
        }
      }
    }
  }
}
