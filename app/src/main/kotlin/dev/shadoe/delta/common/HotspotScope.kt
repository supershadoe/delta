package dev.shadoe.delta.common

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun HotspotScope(
  vm: HotspotScopeViewModel = viewModel(),
  content: @Composable () -> Unit,
) {
  content()
}
