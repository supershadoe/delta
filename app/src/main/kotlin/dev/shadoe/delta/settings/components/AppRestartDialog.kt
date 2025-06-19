package dev.shadoe.delta.settings.components

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Autorenew
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import dev.shadoe.delta.R

@Composable
fun AppRestartDialog(onRestart: () -> Unit) {
  AlertDialog(
    onDismissRequest = {},
    confirmButton = {
      TextButton(onClick = onRestart) {
        Text(stringResource(R.string.restart_app))
      }
    },
    icon = {
      Icon(
        imageVector = Icons.Rounded.Autorenew,
        contentDescription = stringResource(R.string.data_export_field_icon),
      )
    },
    title = { Text(text = stringResource(R.string.restart_required)) },
    text = {
      LazyColumn {
        item {
          Text(
            text =
              stringResource(R.string.restart_required_import_succcess_desc)
          )
        }
      }
    },
  )
}
