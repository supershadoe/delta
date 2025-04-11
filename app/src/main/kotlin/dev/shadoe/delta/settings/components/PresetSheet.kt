package dev.shadoe.delta.settings.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.SaveAlt
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.unit.dp
import dev.shadoe.delta.R
import dev.shadoe.delta.data.database.models.Preset

@ExperimentalMaterial3Api
@Composable
fun PresetSheet(
  sheetState: SheetState,
  presets: List<Preset>,
  onDismissRequest: () -> Unit,
  timestampToStringConverter: (Long) -> String,
  applyPreset: (Preset) -> Unit,
  deletePreset: (Preset) -> Unit,
) {
  ModalBottomSheet(
    sheetState = sheetState,
    onDismissRequest = onDismissRequest,
  ) {
    Column(
      horizontalAlignment = Alignment.CenterHorizontally,
      modifier =
        Modifier.padding(horizontal = 24.dp, vertical = 16.dp).fillMaxWidth(),
    ) {
      Text(
        text = stringResource(R.string.presets_setting),
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        style = MaterialTheme.typography.headlineMedium,
      )
      if (presets.isEmpty()) {
        Box(modifier = Modifier.padding(top = 24.dp)) {
          Text(text = stringResource(R.string.presets_none_saved))
        }
      }
      LazyColumn(
        contentPadding = PaddingValues(vertical = 32.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
      ) {
        items(presets) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
          ) {
            Column(modifier = Modifier.fillMaxWidth(0.7f)) {
              Text(
                text =
                  buildAnnotatedString {
                    append(it.presetName)
                    pushStyle(
                      style =
                        SpanStyle(
                          color =
                            MaterialTheme.colorScheme.onSurfaceVariant.copy(
                              alpha = 0.6f
                            )
                        )
                    )
                    append(" (#${it.id})")
                  },
                style = MaterialTheme.typography.titleLarge,
              )
              Text(
                text =
                  stringResource(
                    R.string.preset_saved_on,
                    timestampToStringConverter(it.timestamp),
                  ),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
              )
            }
            Row {
              IconButton(onClick = { deletePreset(it) }) {
                Icon(
                  imageVector = Icons.Rounded.Delete,
                  contentDescription =
                    stringResource(R.string.preset_delete_button),
                )
              }
              IconButton(onClick = { applyPreset(it) }) {
                Icon(
                  imageVector = Icons.Rounded.SaveAlt,
                  contentDescription =
                    stringResource(R.string.preset_apply_button),
                )
              }
            }
          }
        }
      }
    }
  }
}
