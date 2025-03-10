package dev.shadoe.delta.setup.components

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import dev.shadoe.delta.R

@Composable
internal fun ShizukuConnected(continueAction: () -> Unit) {
  Column {
    Text(stringResource(R.string.shizuku_connected_desc))
    Button(onClick = continueAction) {
      Text(text = stringResource(R.string.setup_continue_button))
    }
  }
}
