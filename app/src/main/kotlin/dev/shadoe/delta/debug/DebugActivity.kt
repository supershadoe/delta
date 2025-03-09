package dev.shadoe.delta.debug

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import dev.shadoe.delta.R
import dev.shadoe.delta.typography.Typography

class DebugActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    enableEdgeToEdge()
    super.onCreate(savedInstanceState)
    setContent {
      val colorScheme =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
          dynamicDarkColorScheme(LocalContext.current)
        } else {
          darkColorScheme()
        }

      MaterialTheme(colorScheme = colorScheme, typography = Typography.value) {
        Scaffold(
          topBar = {
            @OptIn(ExperimentalMaterial3Api::class)
            TopAppBar(
              title = { Text(text = stringResource(R.string.debug_title)) }
            )
          }
        ) {
          Column(
            Modifier.fillMaxSize()
              .padding(it)
              .padding(vertical = 8.dp, horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
          ) {
            Button(onClick = { throw RuntimeException("thrown by app") }) {
              Text(text = stringResource(R.string.debug_trigger_crash))
            }
          }
        }
      }
    }
  }
}
