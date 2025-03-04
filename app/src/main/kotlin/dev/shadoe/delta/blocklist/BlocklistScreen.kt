package dev.shadoe.delta.blocklist

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import dev.shadoe.delta.R
import dev.shadoe.delta.navigation.LocalNavController
import dev.shadoe.delta.presentation.hotspot.BlockListViewModel
import kotlinx.coroutines.launch

@Composable
fun BlocklistScreen(
  modifier: Modifier = Modifier,
  vm: BlockListViewModel = viewModel(),
) {
  val navController = LocalNavController.current
  val scope = rememberCoroutineScope()
  val snackbarHostState = remember { SnackbarHostState() }

  val blockedClients by vm.blockedClients.collectAsState(emptyList())

  val blocklistUnblockedText = stringResource(R.string.blocklist_unblocked)
  val noClientHostnameText = stringResource(R.string.no_client_hostname)

  Scaffold(
    topBar = {
      @OptIn(ExperimentalMaterial3Api::class)
      LargeTopAppBar(
        title = { Text(text = stringResource(R.string.blocklist)) },
        navigationIcon = {
          IconButton(onClick = { navController?.navigateUp() }) {
            Icon(
              imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
              contentDescription = stringResource(R.string.back_button),
            )
          }
        },
      )
    },
    snackbarHost = { SnackbarHost(snackbarHostState) },
  ) {
    if (blockedClients.isEmpty()) {
      Box(
        modifier = Modifier.padding(it).fillMaxSize(),
        contentAlignment = Alignment.Center,
      ) {
        Text(
          text = stringResource(R.string.blocklist_none_blocked),
          modifier = Modifier.padding(16.dp),
        )
      }
    }
    LazyColumn(modifier = Modifier.padding(it).then(modifier)) {
      items(blockedClients.size) {
        val d = blockedClients[it]
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
              vm.unblockDevice(blockedClients[it])
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
