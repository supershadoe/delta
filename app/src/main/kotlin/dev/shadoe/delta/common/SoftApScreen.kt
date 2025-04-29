package dev.shadoe.delta.common

import android.app.Activity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import dev.shadoe.delta.R
import dev.shadoe.delta.control.ControlScreen
import dev.shadoe.delta.control.ControlViewModel
import dev.shadoe.delta.control.components.AppBarWithDebugAction
import dev.shadoe.delta.settings.SettingsScreen
import dev.shadoe.delta.settings.SettingsViewModel
import kotlinx.coroutines.launch

@Composable
fun SoftApScreen(vm: SoftApScreenViewModel = viewModel()) {
  val context = LocalContext.current
  val navController = LocalNavController.current
  val scope = rememberCoroutineScope()
  val snackbarHostState = remember { SnackbarHostState() }
  val pagerState =
    rememberPagerState(pageCount = { SoftApScreenDestinations.entries.size })

  val isSoftApSupported by vm.isSoftApSupported.collectAsState(initial = true)

  Box(
    modifier =
      Modifier.background(MaterialTheme.colorScheme.background).fillMaxWidth(),
    contentAlignment = Alignment.Center,
  ) {
    Scaffold(
      modifier = Modifier.widthIn(max = 2160.dp),
      topBar = {
        @OptIn(ExperimentalMaterial3Api::class)
        CenterAlignedTopAppBar(
          title = {
            AppBarWithDebugAction(
              onNavigateToDebug = { navController.navigate(Routes.DebugScreen) }
            )
          }
        )
      },
      snackbarHost = { SnackbarHost(snackbarHostState) },
    ) { scaffoldPadding ->
      Column(modifier = Modifier.padding(scaffoldPadding).fillMaxWidth()) {
        TabRow(selectedTabIndex = pagerState.currentPage) {
          SoftApScreenDestinations.entries.forEach {
            Tab(
              selected = pagerState.currentPage == it.ordinal,
              onClick = {
                scope.launch { pagerState.animateScrollToPage(it.ordinal) }
              },
              text = { Text(stringResource(it.label)) },
              icon = {
                Icon(
                  imageVector = it.icon,
                  contentDescription = stringResource(it.contentDescription),
                )
              },
            )
          }
        }

        HorizontalPager(
          state = pagerState,
          modifier = Modifier.fillMaxWidth().weight(1f),
        ) {
          Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.TopCenter,
          ) {
            when (SoftApScreenDestinations.entries[it]) {
              SoftApScreenDestinations.CONTROL -> {
                val vm = hiltViewModel<ControlViewModel>()
                ControlScreen(
                  onShowSnackbar = { visuals ->
                    scope.launch { snackbarHostState.showSnackbar(visuals) }
                  },
                  vm = vm,
                )
              }

              SoftApScreenDestinations.SETTINGS -> {
                val vm = hiltViewModel<SettingsViewModel>()
                SettingsScreen(
                  onShowSnackbar = { visuals ->
                    scope.launch { snackbarHostState.showSnackbar(visuals) }
                  },
                  vm = vm,
                )
              }
            }
          }
        }
      }
    }
  }

  if (!isSoftApSupported) {
    AlertDialog(
      onDismissRequest = {},
      confirmButton = {
        TextButton(onClick = { (context as? Activity)?.finish() }) {
          Text(text = stringResource(R.string.close_button))
        }
      },
      text = { Text(text = stringResource(R.string.hotspot_not_supported)) },
    )
  }
}
