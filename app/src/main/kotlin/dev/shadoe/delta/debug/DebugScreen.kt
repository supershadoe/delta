package dev.shadoe.delta.debug

import android.os.Build
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import dev.shadoe.delta.R
import dev.shadoe.delta.design.AppTheme

@Composable
fun DebugScreen(onNavigateUp: (() -> Unit)?, vm: DebugViewModel = viewModel()) {
  val metadata = remember {
    """
        Manufacturer: ${Build.MANUFACTURER} (${Build.BRAND})
        Model: ${Build.MODEL} (${Build.DEVICE})
        OS: Android ${Build.VERSION.RELEASE_OR_CODENAME} (${Build.VERSION.SDK_INT}
        Fingerprint: ${Build.FINGERPRINT}
        """
      .trimIndent()
  }

  val config by vm.config.collectAsState()
  val status by vm.status.collectAsState()
  AppTheme {
    Scaffold(
      topBar = {
        @OptIn(ExperimentalMaterial3Api::class)
        TopAppBar(
          title = { Text(text = stringResource(R.string.debug_title)) },
          navigationIcon = {
            if (onNavigateUp != null) {
              IconButton(onClick = onNavigateUp) {
                Icon(
                  imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                  contentDescription = stringResource(R.string.back_button),
                )
              }
            }
          },
        )
      }
    ) {
      LazyColumn(
        modifier =
          Modifier.padding(it).padding(vertical = 8.dp, horizontal = 24.dp)
      ) {
        item {
          Text(text = metadata, modifier = Modifier.padding(bottom = 8.dp))
        }
        item {
          Text(
            text = "Current config",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(top = 8.dp),
          )
          Text(text = "$config", modifier = Modifier.padding(bottom = 8.dp))
        }
        item {
          Text(
            text = "Current status",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(top = 8.dp),
          )
          Text(text = "$status", modifier = Modifier.padding(bottom = 8.dp))
        }
        item {
          Button(
            onClick = {
              throw RuntimeException("manually triggered from debug screen")
            },
            modifier = Modifier.padding(top = 8.dp),
          ) {
            Text(text = stringResource(R.string.debug_trigger_crash))
          }
        }
      }
    }
  }
}
