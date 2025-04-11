package dev.shadoe.delta.settings.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import dev.shadoe.delta.R

@Composable
fun PresetSaveDialog(onSave: (String) -> Unit, onDismiss: () -> Unit) {
  var presetName by remember { mutableStateOf("") }
  AlertDialog(
    onDismissRequest = onDismiss,
    confirmButton = {
      TextButton(onClick = { onSave(presetName) }) {
        Text(stringResource(R.string.save_button))
      }
    },
    dismissButton = {
      TextButton(onClick = onDismiss) {
        Text(stringResource(R.string.cancel_button))
      }
    },
    title = { Text(stringResource(R.string.preset_save_title)) },
    text = {
      TextField(value = presetName, onValueChange = { presetName = it })
    },
  )
}
