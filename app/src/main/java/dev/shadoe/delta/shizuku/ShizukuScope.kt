package dev.shadoe.delta.shizuku

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.livedata.observeAsState
import androidx.lifecycle.viewmodel.compose.viewModel

val LocalShizukuRunning = compositionLocalOf { false }
val LocalShizukuConnected = compositionLocalOf { false }

@Composable
fun ShizukuScope(
    shizukuViewModel: ShizukuViewModel = viewModel(),
    content: @Composable () -> Unit
) {
    val shizukuConnected = shizukuViewModel.isConnected.observeAsState(false)
    val shizukuRunning = shizukuViewModel.isRunning.observeAsState(false)

    CompositionLocalProvider(
        LocalShizukuRunning provides shizukuRunning.value,
        LocalShizukuConnected provides shizukuConnected.value,
        content = content
    )
}
