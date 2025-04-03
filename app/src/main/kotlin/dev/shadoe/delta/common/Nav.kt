package dev.shadoe.delta.common

import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
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
  val isSetupNeeded by vm.isSetupNeeded.collectAsState()
  NavHost(
    navController = navController,
    startDestination =
      if (isSetupNeeded) {
        Routes.Setup
      } else {
        Routes.HotspotScreen
      },
    enterTransition = { slideInHorizontally() },
    exitTransition = { slideOutHorizontally() },
  ) {
    navigation<Routes.Setup>(startDestination = Routes.Setup.FirstUseScreen) {
      composable<Routes.Setup.FirstUseScreen> {
        FirstUseScreen(
          onStartSetup = {
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
