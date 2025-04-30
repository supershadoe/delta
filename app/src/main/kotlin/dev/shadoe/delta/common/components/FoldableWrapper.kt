package dev.shadoe.delta.common.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material.icons.rounded.KeyboardArrowUp
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import dev.shadoe.delta.R

@Composable
fun FoldableWrapper(
  text: String,
  foldableState: Boolean,
  onFoldableToggled: () -> Unit,
  modifier: Modifier = Modifier,
) {
  Row(
    modifier =
      Modifier.fillMaxWidth()
        .clickable { onFoldableToggled() }
        .padding(vertical = 16.dp)
        .then(modifier),
    horizontalArrangement = Arrangement.SpaceBetween,
  ) {
    Text(
      text = text,
      color = MaterialTheme.colorScheme.onSurfaceVariant,
      style = MaterialTheme.typography.titleLarge,
    )
    Icon(
      modifier = Modifier.align(Alignment.CenterVertically),
      imageVector =
        if (foldableState) {
          Icons.Rounded.KeyboardArrowUp
        } else {
          Icons.Rounded.KeyboardArrowDown
        },
      contentDescription =
        if (foldableState) {
          stringResource(R.string.collapse_icon)
        } else {
          stringResource(R.string.expand_icon)
        },
    )
  }
}
