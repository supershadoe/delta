package dev.shadoe.delta.hotspot

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.lifecycle.viewmodel.compose.viewModel
import dev.shadoe.hotspotapi.HotspotApi

val LocalHotspotApiInstance = compositionLocalOf<HotspotApi?> { null }

@Composable
fun HotspotApiScope(
    hotspotApiViewModel: HotspotApiViewModel = viewModel(),
    content: @Composable () -> Unit
) {
    CompositionLocalProvider(
        LocalHotspotApiInstance provides hotspotApiViewModel.hotspotApi,
        content = content
    )
}
