package dev.shadoe.delta.hotspot.navigation

import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import dev.shadoe.delta.hotspot.BlocklistScreen
import dev.shadoe.delta.hotspot.HotspotApiScope
import dev.shadoe.delta.hotspot.HotspotEditScreen
import dev.shadoe.delta.hotspot.HotspotScreen
import dev.shadoe.delta.presentation.hotspot.BlockListViewModel
import dev.shadoe.delta.presentation.hotspot.EditScreenViewModel
import dev.shadoe.delta.presentation.hotspot.HotspotControlViewModel

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
                composable<Routes.HotspotScreen> {
                    val vm = hiltViewModel<HotspotControlViewModel>()
                    HotspotScreen(vm = vm)
                }
                composable<Routes.HotspotEditScreen> {
                    val vm = hiltViewModel<EditScreenViewModel>()
                    HotspotEditScreen(vm = vm)
                }
                composable<Routes.BlocklistScreen> {
                    val vm = hiltViewModel<BlockListViewModel>()
                    BlocklistScreen(vm = vm)
                }
            }
        }
    }
}
