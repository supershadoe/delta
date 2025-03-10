package dev.shadoe.delta.setup.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import dev.shadoe.delta.R
import dev.shadoe.delta.setup.ShizukuSetupViewModel

@Composable
internal fun ShizukuNotRunning(modifier: Modifier = Modifier) {
  val context = LocalContext.current
  Column(modifier = modifier) {
    Column(
      modifier = Modifier.weight(0.5f),
      verticalArrangement = Arrangement.Center,
    ) {
      Text(
        text = stringResource(R.string.shizuku_setup_title),
        style = MaterialTheme.typography.displaySmall,
      )
      Text(
        text = stringResource(R.string.shizuku_setup_not_running),
        style = MaterialTheme.typography.titleLarge,
      )
    }
    Box(
      modifier = Modifier.weight(0.7f).fillMaxWidth(),
      contentAlignment = Alignment.CenterEnd,
    ) {
      Button(
        onClick = {
          with(context) {
            startActivity(
              packageManager.getLaunchIntentForPackage(
                ShizukuSetupViewModel.SHIZUKU_APP_ID
              )
            )
          }
        }
      ) {
        Text(text = stringResource(R.string.shizuku_start))
      }
    }
    Text(
      text = stringResource(R.string.setup_note),
      style = MaterialTheme.typography.bodySmall,
      color = MaterialTheme.colorScheme.onSurfaceVariant,
      modifier = Modifier.padding(bottom = 32.dp),
    )
  }
}
