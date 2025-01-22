package dev.shadoe.delta.hotspot

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import dev.shadoe.delta.shizuku.LocalShizukuConnected

val LocalHotspotApiInstance = compositionLocalOf<HotspotApi?> { null }

@Composable
fun HotspotApiScope(
    content: @Composable () -> Unit
) {
    val context = LocalContext.current
    val hotspotApi = if (LocalShizukuConnected.current) {
        remember { HotspotApi(context.packageName, context.attributionTag) }
    } else null
    DisposableEffect(hotspotApi) {
        hotspotApi?.registerCallback()
        onDispose {
            hotspotApi?.unregisterCallback()
        }
    }
    CompositionLocalProvider(
        LocalHotspotApiInstance provides hotspotApi, content = content
    )
}
