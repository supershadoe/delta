package dev.shadoe.delta.common

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
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
  Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
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
      },
      modifier = Modifier.widthIn(max = 2160.dp),
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
}
