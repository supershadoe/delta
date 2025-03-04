package dev.shadoe.delta.shizuku

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun ShizukuScope(
  vm: ShizukuViewModel = viewModel(),
  content: @Composable (vm: ShizukuViewModel) -> Unit,
) {
  content(vm)
}
