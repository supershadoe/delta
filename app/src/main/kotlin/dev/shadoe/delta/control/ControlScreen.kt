package dev.shadoe.delta.control

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarVisuals
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import dev.shadoe.delta.R
import dev.shadoe.delta.api.ACLDevice
import dev.shadoe.delta.api.SoftApEnabledState
import dev.shadoe.delta.control.components.BlocklistComponentActions
import dev.shadoe.delta.control.components.BlocklistComponentState
import dev.shadoe.delta.control.components.SoftApControl
import dev.shadoe.delta.control.components.SoftApControlViewModel
import dev.shadoe.delta.control.components.blocklistComponent
import dev.shadoe.delta.control.components.connectedClientsList

@Composable
fun ControlScreen(
  modifier: Modifier = Modifier,
  onShowSnackbar: (SnackbarVisuals) -> Unit = {},
  vm: ControlViewModel = viewModel(),
) {
  var isBlocklistShown by remember { mutableStateOf(false) }
  var devicesToUnblock by
    remember(isBlocklistShown) { mutableStateOf(setOf<ACLDevice>()) }

  val enabledState by
    vm.enabledState.collectAsState(SoftApEnabledState.WIFI_AP_STATE_DISABLED)
  val tetheredClients by vm.connectedClients.collectAsState(listOf())
  val blockedClients by vm.blockedClients.collectAsState(listOf())
  val supportsBlocklist by vm.supportsBlocklist.collectAsState(false)

  val featureNotSupportedSnackbar =
    object : SnackbarVisuals {
      override val message = stringResource(R.string.feature_not_supported)
      override val withDismissAction = true
      override val duration = SnackbarDuration.Short
      override val actionLabel = null
    }
  val clientUnblockedSnackbar =
    object : SnackbarVisuals {
      override val message = stringResource(R.string.blocklist_unblocked)
      override val duration = SnackbarDuration.Short
      override val withDismissAction = true
      override val actionLabel = null
    }

  LazyColumn(modifier = modifier) {
    item {
      val compVm = hiltViewModel<SoftApControlViewModel>()
      Box(modifier = Modifier.padding(vertical = 16.dp)) {
        SoftApControl(
          onFailedToShowQr = { onShowSnackbar(featureNotSupportedSnackbar) },
          vm = compVm,
        )
      }
    }
    if (enabledState == SoftApEnabledState.WIFI_AP_STATE_ENABLED) {
      connectedClientsList(
        tetheredClients,
        supportsBlocklist,
        onBlockClient = { vm.blockDevice(it) },
      )
    }
    if (supportsBlocklist) {
      blocklistComponent(
        state =
          BlocklistComponentState(
            isBlocklistShown = isBlocklistShown,
            blockedClients = blockedClients,
            devicesToUnblock = devicesToUnblock,
          ),
        actions =
          BlocklistComponentActions(
            addToUnblockList = { devicesToUnblock += it },
            removeFromUnblockList = { devicesToUnblock -= it },
            onBlocklistToggled = { isBlocklistShown = !isBlocklistShown },
            onUnblockClients = {
              vm.unblockDevices(devicesToUnblock)
              devicesToUnblock = setOf()
              onShowSnackbar(clientUnblockedSnackbar)
            },
          ),
      )
    }
  }
}
