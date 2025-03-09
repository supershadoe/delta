package dev.shadoe.delta.control

import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Block
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import dev.shadoe.delta.R
import dev.shadoe.delta.api.SoftApEnabledState
import dev.shadoe.delta.control.buttons.HotspotButton
import dev.shadoe.delta.control.components.ConnectedDevicesList
import dev.shadoe.delta.control.components.PassphraseDisplay
import dev.shadoe.delta.debug.DebugActivity
import dev.shadoe.delta.navigation.LocalNavController
import dev.shadoe.delta.navigation.Routes
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ControlScreen(
  modifier: Modifier = Modifier,
  vm: ControlViewModel = viewModel(),
) {
  val sheetState = rememberModalBottomSheetState()
  val showConnectedDevices = remember { mutableStateOf(false) }
  val scope = rememberCoroutineScope()
  val snackbarHostState = remember { SnackbarHostState() }
  val navController = LocalNavController.current
  val isBigScreen = LocalConfiguration.current.screenWidthDp >= 700
  // TODO: remove this comment
  //    println(
  //        "isBigScreen: $isBigScreen; screenWidthDp:
  // ${LocalConfiguration.current.screenWidthDp}",
  //    )

  // Debug screen trigger
  val context = LocalContext.current
  var appNameTaps by remember { mutableIntStateOf(0) }
  val shouldTriggerDebugScreen by remember {
    derivedStateOf { (appNameTaps + 1) % 5 == 0 }
  }

  val ssid by vm.ssid.collectAsState("")
  val passphrase by vm.passphrase.collectAsState("")
  val enabledState by
    vm.enabledState.collectAsState(SoftApEnabledState.WIFI_AP_STATE_DISABLED)
  val shouldShowPassphrase by vm.shouldShowPassphrase.collectAsState(true)
  val tetheredClientCount by vm.tetheredClientCount.collectAsState(0)

  val noConnectedDevicesText =
    stringResource(id = R.string.no_connected_devices)
  val hotspotNotEnabledText = stringResource(id = R.string.hotspot_not_enabled)
  val hotspotEnableActionText =
    stringResource(id = R.string.hotspot_enable_action)

  val onConnectedDevicesClicked = {
    if (enabledState == SoftApEnabledState.WIFI_AP_STATE_ENABLED) {
      if (tetheredClientCount == 0) {
        if (snackbarHostState.currentSnackbarData == null) {
          scope.launch {
            snackbarHostState.showSnackbar(
              message = noConnectedDevicesText,
              withDismissAction = true,
              duration = SnackbarDuration.Short,
            )
          }
        }
      } else {
        showConnectedDevices.value = true
      }
    } else if (snackbarHostState.currentSnackbarData == null) {
      scope.launch {
        val result =
          snackbarHostState.showSnackbar(
            message = hotspotNotEnabledText,
            actionLabel = hotspotEnableActionText,
            withDismissAction = true,
            duration = SnackbarDuration.Short,
          )
        if (result == SnackbarResult.ActionPerformed) {
          vm.startHotspot()
        }
      }
    }
  }

  LaunchedEffect(shouldTriggerDebugScreen) {
    if (!shouldTriggerDebugScreen) return@LaunchedEffect
    context.startActivity(Intent(context, DebugActivity::class.java))
    Toast.makeText(context, "Triggered debug screen", Toast.LENGTH_SHORT).show()
  }

  Row(modifier = modifier) {
    Scaffold(
      modifier = Modifier.weight(1f),
      topBar = {
        CenterAlignedTopAppBar(
          title = {
            Text(
              text = stringResource(id = R.string.app_name),
              modifier = Modifier.clickable { appNameTaps += 1 },
            )
          },
          actions = {
            IconButton(
              onClick = { navController?.navigate(Routes.BlocklistScreen) }
            ) {
              Icon(
                imageVector = Icons.Rounded.Block,
                contentDescription = stringResource(id = R.string.blocklist),
              )
            }
          },
        )
      },
      floatingActionButton = {
        FloatingActionButton(
          onClick = {
            navController?.navigate(route = Routes.HotspotEditScreen)
          }
        ) {
          Icon(
            imageVector = Icons.Rounded.Edit,
            contentDescription = stringResource(id = R.string.edit_button),
          )
        }
      },
      snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
    ) { scaffoldPadding ->
      Column(
        modifier = Modifier.padding(scaffoldPadding).fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
      ) {
        Box(
          modifier = Modifier.weight(1f),
          contentAlignment = Alignment.Center,
        ) {
          HotspotButton(
            enabledState = enabledState,
            startHotspot = { vm.startHotspot(it) },
            stopHotspot = { vm.stopHotspot() },
          )
        }
        Column(
          horizontalAlignment = Alignment.CenterHorizontally,
          verticalArrangement = Arrangement.Bottom,
        ) {
          Text(text = ssid ?: stringResource(id = R.string.no_ssid))
          Box(modifier = Modifier.padding(bottom = 16.dp)) {
            if (shouldShowPassphrase) {
              PassphraseDisplay(passphrase = passphrase)
            }
          }
          if (!isBigScreen) {
            TextButton(onClick = onConnectedDevicesClicked) {
              val style =
                MaterialTheme.typography.bodyMedium.copy(
                  textDecoration = TextDecoration.Underline
                )
              Text(
                stringResource(
                  id = R.string.connected_devices_button,
                  tetheredClientCount,
                ),
                style = style,
              )
            }
          }
        }
      }
      if (showConnectedDevices.value) {
        ModalBottomSheet(
          onDismissRequest = { showConnectedDevices.value = false },
          sheetState = sheetState,
        ) {
          val vm = hiltViewModel<ConnectedDevicesViewModel>()
          ConnectedDevicesList(vm)
        }
      }
    }
    if (isBigScreen) {
      Scaffold(modifier = Modifier.weight(1f)) {
        val background = MaterialTheme.colorScheme.surfaceContainerLowest
        Box(
          modifier = Modifier.background(background).padding(it).fillMaxHeight()
        ) {
          val vm = hiltViewModel<ConnectedDevicesViewModel>()
          ConnectedDevicesList(vm)
        }
      }
    }
  }
}
