package dev.shadoe.delta.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material.icons.rounded.KeyboardArrowUp
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
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import dev.shadoe.delta.R
import dev.shadoe.delta.api.SoftApAutoShutdownTimeout
import dev.shadoe.delta.api.SoftApSecurityType
import dev.shadoe.delta.common.LocalNavController
import dev.shadoe.delta.settings.components.AutoShutDownTimeOutField
import dev.shadoe.delta.settings.components.AutoShutdownField
import dev.shadoe.delta.settings.components.FrequencyBandField
import dev.shadoe.delta.settings.components.HiddenHotspotField
import dev.shadoe.delta.settings.components.MacRandomizationField
import dev.shadoe.delta.settings.components.MaxClientLimitField
import dev.shadoe.delta.settings.components.PassphraseField
import dev.shadoe.delta.settings.components.SecurityTypeField
import dev.shadoe.delta.settings.components.SsidField
import kotlinx.coroutines.launch

@Composable
fun SettingsScreen(
  modifier: Modifier = Modifier,
  vm: SettingsViewModel = viewModel(),
) {
  val navController = LocalNavController.current
  val focusManager = LocalFocusManager.current
  val scope = rememberCoroutineScope()
  val snackbarHostState = remember { SnackbarHostState() }

  val config by vm.config.collectAsState()
  val status by vm.status.collectAsState()

  val passphraseEmptyWarningText =
    stringResource(R.string.passphrase_empty_warning)
  val failedToSaveText = stringResource(R.string.save_changes_failed_warning)

  var isAdvancedSettingsEnabled by remember { mutableStateOf(false) }

  @OptIn(ExperimentalMaterial3Api::class)
  val appBarScrollBehavior =
    TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

  Scaffold(
    topBar = {
      @OptIn(ExperimentalMaterial3Api::class)
      LargeTopAppBar(
        title = { Text(text = stringResource(R.string.settings)) },
        navigationIcon = {
          IconButton(onClick = { navController?.navigateUp() }) {
            Icon(
              imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
              contentDescription = stringResource(R.string.back_button),
            )
          }
        },
        scrollBehavior = appBarScrollBehavior,
      )
    },
    modifier =
      Modifier.nestedScroll(
        @OptIn(ExperimentalMaterial3Api::class)
        appBarScrollBehavior.nestedScrollConnection
      ),
    snackbarHost = { SnackbarHost(snackbarHostState) },
  ) { scaffoldPadding ->
    LazyColumn(
      modifier =
        Modifier.fillMaxSize()
          .padding(scaffoldPadding)
          .padding(horizontal = 16.dp)
          .pointerInput(Unit) {
            detectTapGestures(onTap = { focusManager.clearFocus() })
          }
          .then(modifier)
    ) {
      item {
        Text(
          text = stringResource(R.string.settings_desc),
          modifier = Modifier.padding(8.dp),
        )
      }
      item {
        SsidField(
          ssid = config.ssid ?: "",
          onSsidChange = { vm.updateSsid(it) },
        )
      }
      item {
        SecurityTypeField(
          securityType = config.securityType,
          supportedSecurityTypes = status.capabilities.supportedSecurityTypes,
          onSecurityTypeChange = { vm.updateSecurityType(it) },
        )
      }
      if (config.securityType != SoftApSecurityType.SECURITY_TYPE_OPEN) {
        item {
          PassphraseField(
            passphrase = config.passphrase,
            onPassphraseChange = { vm.updatePassphrase(it) },
          )
        }
      }
      item {
        AutoShutdownField(
          isAutoShutdownEnabled = config.isAutoShutdownEnabled,
          onAutoShutdownChange = { vm.updateAutoShutdown(it) },
        )
      }
      item {
        FrequencyBandField(
          frequencyBand = config.speedType,
          supportedBands = status.capabilities.supportedFrequencyBands,
          onBandChange = { vm.updateSpeedType(it) },
        )
      }

      item {
        Row(
          modifier =
            Modifier.fillMaxWidth().clickable {
              isAdvancedSettingsEnabled = !isAdvancedSettingsEnabled
            },
          horizontalArrangement = Arrangement.SpaceBetween,
        ) {
          Text(
            text = stringResource(R.string.advanced_settings_field_label),
            modifier = Modifier.padding(8.dp),
          )
          Icon(
            modifier = Modifier.align(Alignment.CenterVertically),
            imageVector =
              if (isAdvancedSettingsEnabled) {
                Icons.Rounded.KeyboardArrowUp
              } else {
                Icons.Rounded.KeyboardArrowDown
              },
            contentDescription =
              if (isAdvancedSettingsEnabled) {
                stringResource(R.string.collapse_advanced_settings_icon)
              } else {
                stringResource(R.string.expand_advanced_settings_icon)
              },
          )
        }
      }
      if (isAdvancedSettingsEnabled) {
        item {
          HiddenHotspotField(
            isHiddenHotspotEnabled = config.isHidden,
            onHiddenHotspotChange = { vm.updateIsHidden(it) },
          )
        }
        item {
          MaxClientLimitField(
            maxClient = status.capabilities.maxSupportedClients,
            onMaxClientChange = { vm.updateMaxClientLimit(it.toInt()) },
            allowedLimit = config.maxClientLimit,
          )
        }

        item {
          MacRandomizationField(
            macRandomizationSetting = config.macRandomizationSetting,
            onSettingChange = { vm.updateMacRandomizationSetting(it) },
          )
        }

        item {
          AutoShutDownTimeOutField(
            autoShutDownTimeOut =
              config.autoShutdownTimeout.takeIf {
                it in SoftApAutoShutdownTimeout.supportedShutdownTimeouts
              } ?: SoftApAutoShutdownTimeout.DEFAULT,
            supportedAutoShutdownType =
              SoftApAutoShutdownTimeout.supportedShutdownTimeouts,
            onAutoShutdownChange = { vm.updateAutoShutdownTimeout(it) },
          )
        }
      }

      item {
        Button(
          onClick = onClick@{
              if (
                config.passphrase.isEmpty() &&
                  config.securityType != SoftApSecurityType.SECURITY_TYPE_OPEN
              ) {
                scope.launch {
                  snackbarHostState.showSnackbar(
                    message = passphraseEmptyWarningText,
                    withDismissAction = true,
                    duration = SnackbarDuration.Short,
                  )
                }
                return@onClick
              }
              if (!vm.commit()) {
                scope.launch {
                  snackbarHostState.showSnackbar(
                    message = failedToSaveText,
                    withDismissAction = true,
                    duration = SnackbarDuration.Short,
                  )
                }
              } else {
                navController?.navigateUp()
              }
            }
        ) {
          Text(text = stringResource(R.string.save_button))
        }
      }
    }
  }
}
