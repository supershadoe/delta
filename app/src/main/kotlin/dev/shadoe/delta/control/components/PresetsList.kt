package dev.shadoe.delta.control.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import dev.shadoe.delta.R
import dev.shadoe.delta.common.components.FadeInExpanded
import dev.shadoe.delta.common.components.FoldableWrapper
import dev.shadoe.delta.data.database.models.Preset

internal fun LazyListScope.presetsComponent(
  isPresetsShown: Boolean,
  presets: List<Preset>,
  applyPreset: (Preset) -> Unit,
  onPresetsListToggled: () -> Unit,
) {
  item {
    FoldableWrapper(
      text = stringResource(R.string.presets_setting),
      foldableState = isPresetsShown,
      onFoldableToggled = onPresetsListToggled,
      modifier = Modifier.padding(horizontal = 16.dp),
    )
  }
  item {
    FadeInExpanded(visible = isPresetsShown && presets.isEmpty()) {
      Box(
        modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
        contentAlignment = Alignment.Center,
      ) {
        Text(text = stringResource(R.string.presets_none_saved))
      }
    }
  }
  items(presets) {
    FadeInExpanded(visible = isPresetsShown) {
      PresetComponent(it.presetName, applyPreset = { applyPreset(it) })
    }
  }
}
