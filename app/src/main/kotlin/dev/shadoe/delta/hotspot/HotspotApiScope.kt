package dev.shadoe.delta.hotspot

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import dev.shadoe.delta.presentation.hotspot.HotspotScopeViewModel

@Composable
fun HotspotApiScope(
    vm: HotspotScopeViewModel = viewModel(),
    content: @Composable () -> Unit,
) {
    content()
}
