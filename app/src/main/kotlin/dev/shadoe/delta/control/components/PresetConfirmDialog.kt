package dev.shadoe.delta.control.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import dev.shadoe.delta.R
import dev.shadoe.delta.data.database.models.Preset

@Composable
fun PresetConfirmDialog(
  preset: Preset,
  onApply: (Preset) -> Unit,
  onDismiss: () -> Unit,
) {
  AlertDialog(
    onDismissRequest = onDismiss,
    confirmButton = {
      TextButton(onClick = { onApply(preset) }) {
        Text(stringResource(R.string.preset_apply_button))
      }
    },
    dismissButton = {
      TextButton(onClick = onDismiss) {
        Text(stringResource(R.string.cancel_button))
      }
    },
    title = {
      Text(stringResource(R.string.preset_confirm_title, preset.presetName))
    },
    text = { Text(stringResource(R.string.preset_confirm_body)) },
  )
}
