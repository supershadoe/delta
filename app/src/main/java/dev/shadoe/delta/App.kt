package dev.shadoe.delta

import android.os.Build
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import dev.shadoe.delta.hotspot.HotspotApiScope
import dev.shadoe.delta.navigation.Nav.AppNavGraph
import dev.shadoe.delta.shizuku.ShizukuScope

@Composable
fun App() {
    val colorScheme = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        dynamicDarkColorScheme(LocalContext.current)
    } else {
        darkColorScheme()
    }

    MaterialTheme(colorScheme = colorScheme) {
        ShizukuScope {
            HotspotApiScope {
                AppNavGraph()
            }
        }
    }
}
