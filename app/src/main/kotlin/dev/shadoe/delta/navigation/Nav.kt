package dev.shadoe.delta.navigation

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
import dev.shadoe.delta.blocklist.BlockListViewModel
import dev.shadoe.delta.blocklist.BlocklistScreen
import dev.shadoe.delta.common.HotspotScope
import dev.shadoe.delta.control.ControlScreen
import dev.shadoe.delta.control.ControlViewModel
import dev.shadoe.delta.debug.DebugScreen
import dev.shadoe.delta.debug.DebugViewModel
import dev.shadoe.delta.settings.SettingsScreen
import dev.shadoe.delta.settings.SettingsViewModel

val LocalNavController = staticCompositionLocalOf<NavHostController?> { null }

@Composable
fun HotspotNavGraph() {
  val navController = rememberNavController()
  HotspotScope {
    CompositionLocalProvider(LocalNavController provides navController) {
      NavHost(
        navController = LocalNavController.current!!,
        startDestination = Routes.HotspotScreen,
        enterTransition = { scaleIn() + fadeIn() },
        exitTransition = { scaleOut() + fadeOut() },
      ) {
        composable<Routes.HotspotScreen> {
          val vm = hiltViewModel<ControlViewModel>()
          ControlScreen(vm = vm)
        }
        composable<Routes.HotspotEditScreen> {
          val vm = hiltViewModel<SettingsViewModel>()
          SettingsScreen(vm = vm)
        }
        composable<Routes.BlocklistScreen> {
          val vm = hiltViewModel<BlockListViewModel>()
          BlocklistScreen(vm = vm)
        }
        composable<Routes.DebugScreen> {
          val vm = hiltViewModel<DebugViewModel>()
          DebugScreen(vm = vm)
        }
      }
    }
  }
}
