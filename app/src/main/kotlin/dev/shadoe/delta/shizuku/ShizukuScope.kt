package dev.shadoe.delta.shizuku

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.compositionLocalOf
import androidx.lifecycle.viewmodel.compose.viewModel

val LocalShizukuState = compositionLocalOf { NOT_READY }

@Composable
fun ShizukuScope(
    shizukuViewModel: ShizukuViewModel = viewModel(),
    content: @Composable () -> Unit,
) {
    val shizukuState =
        shizukuViewModel.shizukuState.collectAsState(
            initial = NOT_READY,
        )
    CompositionLocalProvider(
        LocalShizukuState provides shizukuState.value,
        content = content,
    )
}
