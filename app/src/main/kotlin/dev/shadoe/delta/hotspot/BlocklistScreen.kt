package dev.shadoe.delta.hotspot

import androidx.compose.foundation.layout.Box
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
import androidx.compose.ui.unit.dp
import dev.shadoe.delta.hotspot.navigation.LocalNavController
import kotlinx.coroutines.launch

@Composable
fun BlocklistScreen(modifier: Modifier = Modifier) {
    val navController = LocalNavController.current
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    val hotspotApi = LocalHotspotApiInstance.current!!
    val config by hotspotApi.config.collectAsState()

    Scaffold(
        topBar = {
            @OptIn(ExperimentalMaterial3Api::class)
            LargeTopAppBar(
                title = { Text(text = "Blocklist") },
                navigationIcon = {
                    IconButton(onClick = { navController?.navigateUp() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                            contentDescription = "Back",
                        )
                    }
                },
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
    ) {
        if (config.blockedDevices.isEmpty()) {
            Box(
                modifier = Modifier.padding(it).fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = "No devices are blocked.",
                    modifier = Modifier.padding(16.dp),
                )
            }
        }
        LazyColumn(
            modifier =
                Modifier
                    .padding(it)
                    .then(modifier),
        ) {
            items(config.blockedDevices.size) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = config.blockedDevices[it].toString(),
                        modifier = Modifier.weight(1f),
                    )
                    Button(onClick = {
                        val d = config.blockedDevices
                        scope.launch {
                            setSoftApConfiguration(
                                hotspotApi = hotspotApi,
                                config =
                                    config.copy(
                                        blockedDevices = d - d[it],
                                    ),
                            )
                            snackbarHostState.showSnackbar(
                                message = "Device unblocked",
                                duration = SnackbarDuration.Short,
                                withDismissAction = true,
                            )
                        }
                    }) {
                        Text(text = "UNBLOCK")
                    }
                }
            }
        }
    }
}
