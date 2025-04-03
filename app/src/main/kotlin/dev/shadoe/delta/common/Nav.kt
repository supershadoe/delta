package dev.shadoe.delta.common

import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.TransformOrigin
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import dev.shadoe.delta.blocklist.BlockListViewModel
import dev.shadoe.delta.blocklist.BlocklistScreen
import dev.shadoe.delta.control.ControlScreen
import dev.shadoe.delta.control.ControlViewModel
import dev.shadoe.delta.debug.DebugScreen
import dev.shadoe.delta.debug.DebugViewModel
import dev.shadoe.delta.settings.SettingsScreen
import dev.shadoe.delta.settings.SettingsViewModel
import dev.shadoe.delta.setup.CrashHandlerSetupScreen
import dev.shadoe.delta.setup.FirstUseScreen
import dev.shadoe.delta.setup.ShizukuSetupScreen
import dev.shadoe.delta.setup.ShizukuSetupViewModel

@Composable
fun Nav(vm: NavViewModel = viewModel()) {
  val navController = rememberNavController()
  val startScreen by vm.startScreen.collectAsState()
  NavHost(
    navController = navController,
    startDestination = startScreen,
    /** Played on the screen that's exiting from view due to back button */
    popExitTransition = {
      scaleOut(
        targetScale = 0.9f,
        transformOrigin = TransformOrigin(1f, 0.5f),
      ) + fadeOut()
    },
    /** Played on the next screen that appears (the previous screen) */
    popEnterTransition = {
      scaleIn(
        initialScale = 0.9f,
        transformOrigin = TransformOrigin(0f, 0.5f),
      ) + fadeIn()
    },
    /** Played on the new screen that is being added to the nav stack */
    enterTransition = {
      slideInHorizontally(initialOffsetX = { it / 2 }) + fadeIn()
    },
    /** Played on the screen that's being layered over by adding a new screen */
    exitTransition = {
      slideOutHorizontally(targetOffsetX = { it / 2 }) + fadeOut()
    },
  ) {
    composable<Routes.BlankScreen> {}
    composable<Routes.Setup.FirstUseScreen> {
      FirstUseScreen(
        onStartSetup = {
          vm.onSetupStarted()
          navController.navigate(route = Routes.Setup.ShizukuSetupScreen)
        }
      )
    }
    composable<Routes.Setup.ShizukuSetupScreen> {
      val vm = hiltViewModel<ShizukuSetupViewModel>()
      ShizukuSetupScreen(
        onSetupFinished = {
          navController.navigate(Routes.Setup.CrashHandlerSetupScreen)
        },
        vm = vm,
      )
    }
    composable<Routes.Setup.CrashHandlerSetupScreen> {
      CrashHandlerSetupScreen(onSetupFinished = { vm.onSetupFinished() })
    }
    composable<Routes.HotspotScreen> {
      val vm = hiltViewModel<ControlViewModel>()
      ControlScreen(
        onNavigateToDebug = { navController.navigate(Routes.DebugScreen) },
        onNavigateToBlocklist = {
          navController.navigate(Routes.BlocklistScreen)
        },
        onNavigateToSettings = {
          navController.navigate(Routes.HotspotEditScreen)
        },
        vm = vm,
      )
    }
    composable<Routes.HotspotEditScreen> {
      val vm = hiltViewModel<SettingsViewModel>()
      SettingsScreen(onNavigateUp = { navController.navigateUp() }, vm = vm)
    }
    composable<Routes.BlocklistScreen> {
      val vm = hiltViewModel<BlockListViewModel>()
      BlocklistScreen(onNavigateUp = { navController.navigateUp() }, vm = vm)
    }
    composable<Routes.DebugScreen> {
      val vm = hiltViewModel<DebugViewModel>()
      DebugScreen(onNavigateUp = { navController.navigateUp() }, vm = vm)
    }
  }
}
