package dev.shadoe.delta.shizuku

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.viewmodel.compose.viewModel
import dev.shadoe.delta.shizuku.ShizukuStates.NOT_READY

@Composable
fun ShizukuScope(
    shizukuViewModel: ShizukuViewModel = viewModel(),
    content: @Composable (currentState: Int) -> Unit,
) {
    val shizukuState =
        shizukuViewModel.shizukuState.collectAsState(initial = NOT_READY)
    content(shizukuState.value)
}
