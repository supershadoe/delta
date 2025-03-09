package dev.shadoe.delta.debug

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
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import dev.shadoe.delta.R
import dev.shadoe.delta.design.AppTheme

class DebugActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    enableEdgeToEdge()
    super.onCreate(savedInstanceState)
    setContent {
      AppTheme {
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
