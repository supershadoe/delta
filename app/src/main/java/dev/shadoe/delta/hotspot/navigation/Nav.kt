package dev.shadoe.delta.hotspot.navigation

import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import dev.shadoe.delta.hotspot.HotspotApiScope
import dev.shadoe.delta.hotspot.settings.HotspotEditScreen
import dev.shadoe.delta.hotspot.HotspotScreen

val LocalNavController = staticCompositionLocalOf<NavHostController?> { null }

@Composable
fun HotspotNavGraph() {
    val navController = rememberNavController()
    HotspotApiScope {
        CompositionLocalProvider(LocalNavController provides navController) {
            NavHost(
                navController = LocalNavController.current!!,
                startDestination = Routes.HotspotScreen,
                enterTransition = {
                    scaleIn() + fadeIn()
                },
                exitTransition = {
                    scaleOut() + fadeOut()
                },
            ) {
                composable<Routes.HotspotScreen> { HotspotScreen() }
                composable<Routes.HotspotEditScreen> { HotspotEditScreen() }
            }
        }
    }
}