package dev.shadoe.delta.control

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ContentCopy
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material.icons.rounded.KeyboardArrowUp
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import dev.shadoe.delta.R
import dev.shadoe.delta.api.ACLDevice
import dev.shadoe.delta.api.SoftApEnabledState
import dev.shadoe.delta.control.components.AppBarWithDebugAction
import dev.shadoe.delta.control.components.SoftApControl
import dev.shadoe.delta.control.components.SoftApControlViewModel
import kotlinx.coroutines.launch

@Composable
fun ControlScreen(
  onNavigateToDebug: () -> Unit,
  modifier: Modifier = Modifier,
  vm: ControlViewModel = viewModel(),
) {
  val clipboardManager = LocalClipboardManager.current
  val density = LocalDensity.current

  val scope = rememberCoroutineScope()
  val snackbarHostState = remember { SnackbarHostState() }
  var isBlocklistShown by remember { mutableStateOf(false) }

  val enabledState by
    vm.enabledState.collectAsState(SoftApEnabledState.WIFI_AP_STATE_DISABLED)
  val tetheredClients by vm.connectedClients.collectAsState(listOf())
  val blockedClients by vm.blockedClients.collectAsState(listOf())
  val supportsBlocklist by vm.supportsBlocklist.collectAsState(false)

  val stringFeatureNotSupported = stringResource(R.string.feature_not_supported)
  val blocklistUnblockedText = stringResource(R.string.blocklist_unblocked)
  val noClientHostnameText = stringResource(R.string.no_client_hostname)

  Scaffold(
    topBar = {
      @OptIn(ExperimentalMaterial3Api::class)
      CenterAlignedTopAppBar(
        title = { AppBarWithDebugAction(onNavigateToDebug) }
      )
    },
    modifier = modifier,
  ) { scaffoldPadding ->
    LazyColumn(modifier = Modifier.padding(scaffoldPadding)) {
      item {
        val compVm = hiltViewModel<SoftApControlViewModel>()
        SoftApControl(
          onFailedToShowQr = {
            scope.launch {
              snackbarHostState.showSnackbar(
                message = stringFeatureNotSupported,
                withDismissAction = true,
                duration = SnackbarDuration.Short,
              )
            }
          },
          vm = compVm,
        )
      }
      item {
        Text(
          text = stringResource(R.string.connected_devices),
          color = MaterialTheme.colorScheme.onSurfaceVariant,
          style = MaterialTheme.typography.titleLarge,
          modifier = Modifier.padding(16.dp),
        )
      }
      if (tetheredClients.isEmpty()) {
        item {
          Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center,
          ) {
            Text(text = stringResource(R.string.no_connected_devices))
          }
        }
      }
      items(tetheredClients) { client ->
        Row(
          modifier =
            Modifier.fillMaxWidth()
              .padding(horizontal = 16.dp, vertical = 4.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically,
        ) {
          Column(modifier = Modifier.padding(horizontal = 8.dp)) {
            Text(
              text =
                client.hostname ?: stringResource(R.string.no_client_hostname)
            )

            client.address?.address?.hostAddress.let { ip ->
              Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier =
                  Modifier.clickable {
                    ip ?: return@clickable
                    clipboardManager.setText(AnnotatedString(ip))
                  },
              ) {
                Text(text = ip ?: stringResource(R.string.ip_not_allocated))

                ip ?: return@Row
                Icon(
                  imageVector = Icons.Rounded.ContentCopy,
                  contentDescription = stringResource(R.string.copy_button),
                  tint = MaterialTheme.colorScheme.primary,
                  modifier =
                    Modifier.size(
                      with(density) { LocalTextStyle.current.fontSize.toDp() }
                    ),
                )
              }
            }
          }
          if (supportsBlocklist) {
            Button(
              onClick = {
                vm.blockDevice(
                  device =
                    ACLDevice(
                      hostname = client.hostname,
                      macAddress = client.macAddress,
                    )
                )
              }
            ) {
              Text(text = stringResource(R.string.block_button))
            }
          }
        }
      }
      takeIf { supportsBlocklist } ?: return@LazyColumn
      item {
        Row(
          modifier =
            Modifier.fillMaxWidth()
              .clickable { isBlocklistShown = !isBlocklistShown }
              .padding(16.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
        ) {
          Text(
            text = stringResource(R.string.blocklist),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.titleLarge,
          )
          Icon(
            modifier = Modifier.align(Alignment.CenterVertically),
            imageVector =
              if (isBlocklistShown) {
                Icons.Rounded.KeyboardArrowUp
              } else {
                Icons.Rounded.KeyboardArrowDown
              },
            contentDescription =
              if (isBlocklistShown) {
                stringResource(R.string.collapse_icon)
              } else {
                stringResource(R.string.expand_icon)
              },
          )
        }
      }
      takeIf { isBlocklistShown } ?: return@LazyColumn
      if (blockedClients.isEmpty()) {
        item {
          Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center,
          ) {
            Text(text = stringResource(R.string.blocklist_none_blocked))
          }
        }
      }
      items(blockedClients) { d ->
        Row(
          modifier = Modifier.padding(16.dp),
          verticalAlignment = Alignment.CenterVertically,
        ) {
          Column(modifier = Modifier.weight(1f)) {
            Text(text = d.hostname ?: noClientHostnameText)
            Text(text = d.macAddress.toString())
          }
          Button(
            onClick = {
              vm.unblockDevice(d)
              scope.launch {
                snackbarHostState.showSnackbar(
                  message = blocklistUnblockedText,
                  duration = SnackbarDuration.Short,
                  withDismissAction = true,
                )
              }
            }
          ) {
            Text(text = stringResource(R.string.unblock_button))
          }
        }
      }
    }
  }
}
