package dev.shadoe.delta.control.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.SaveAlt
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import dev.shadoe.delta.R

@Composable
fun PresetComponent(
  presetName: String,
  applyPreset: () -> Unit,
  modifier: Modifier = Modifier,
) {
  Row(
    modifier =
      modifier.then(Modifier.padding(horizontal = 16.dp, vertical = 4.dp)),
    verticalAlignment = Alignment.CenterVertically,
  ) {
    Column(modifier = Modifier.weight(1f)) {
      Text(text = presetName)
    }
    Row {
      IconButton(onClick = applyPreset) {
        Icon(
          imageVector = Icons.Rounded.SaveAlt,
          contentDescription =
            stringResource(R.string.preset_apply_button),
        )
      }
    }
  }
}
