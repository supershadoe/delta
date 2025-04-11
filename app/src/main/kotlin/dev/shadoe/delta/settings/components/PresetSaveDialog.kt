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

@Composable
fun PresetSaveDialog(onSave: (String) -> Unit, onDismiss: () -> Unit) {
  var presetName by remember { mutableStateOf("") }
  AlertDialog(
    onDismissRequest = onDismiss,
    confirmButton = {
      TextButton(onClick = { onSave(presetName) }) { Text("Save") }
    },
    dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } },
    title = { Text("Provide preset name") },
    text = {
      TextField(value = presetName, onValueChange = { presetName = it })
    },
  )
}
