package dev.shadoe.delta.shizuku

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.compositionLocalOf
import androidx.lifecycle.viewmodel.compose.viewModel

val LocalShizukuRunning = compositionLocalOf { false }
val LocalShizukuConnected = compositionLocalOf { false }
val LocalSuiAvailable = compositionLocalOf { false }

@Composable
fun ShizukuScope(
    shizukuViewModel: ShizukuUtils.ShizukuViewModel = viewModel(),
    content: @Composable () -> Unit
) {
    val shizukuConnected = shizukuViewModel.isConnected.collectAsState(initial = false)
    val shizukuRunning = shizukuViewModel.isRunning.collectAsState(initial = false)
    val isSuiAvailable = shizukuViewModel.isSuiAvailable.collectAsState(initial = false)

    CompositionLocalProvider(
        LocalShizukuRunning provides shizukuRunning.value,
        LocalShizukuConnected provides shizukuConnected.value,
        LocalSuiAvailable provides isSuiAvailable.value,
        content = content
    )
}
