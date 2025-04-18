package dev.shadoe.delta.common

import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import dev.shadoe.delta.control.ControlViewModel
import dev.shadoe.delta.control.NewControlScreen
import dev.shadoe.delta.settings.SettingsScreen
import dev.shadoe.delta.settings.SettingsViewModel

@Composable
fun SoftApScreen() {
  val navController = LocalNavController.current
  var currentDestination by rememberSaveable {
    mutableStateOf(SoftApScreenDestinations.CONTROL)
  }
  NavigationSuiteScaffold(
    navigationSuiteItems = {
      SoftApScreenDestinations.entries.forEach {
        item(
          label = { Text(text = stringResource(it.label)) },
          icon = {
            Icon(
              imageVector = it.icon,
              contentDescription = stringResource(it.label),
            )
          },
          selected = it == currentDestination,
          onClick = { currentDestination = it },
        )
      }
    }
  ) {
    when (currentDestination) {
      SoftApScreenDestinations.CONTROL -> {
        val vm = hiltViewModel<ControlViewModel>()
        NewControlScreen(
          onNavigateToDebug = { navController.navigate(Routes.DebugScreen) },
          vm = vm,
        )
      }
      SoftApScreenDestinations.SETTINGS -> {
        val vm = hiltViewModel<SettingsViewModel>()
        SettingsScreen(vm = vm)
      }
    }
  }
}
