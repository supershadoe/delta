package dev.shadoe.delta.settings

import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.OpenInNew
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material.icons.rounded.KeyboardArrowUp
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
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
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import dev.shadoe.delta.R
import dev.shadoe.delta.api.SoftApAutoShutdownTimeout
import dev.shadoe.delta.api.SoftApSecurityType
import dev.shadoe.delta.settings.components.AutoShutDownTimeOutField
import dev.shadoe.delta.settings.components.AutoShutdownField
import dev.shadoe.delta.settings.components.FrequencyBandField
import dev.shadoe.delta.settings.components.HiddenHotspotField
import dev.shadoe.delta.settings.components.MacRandomizationField
import dev.shadoe.delta.settings.components.MaxClientLimitField
import dev.shadoe.delta.settings.components.PassphraseField
import dev.shadoe.delta.settings.components.PresetField
import dev.shadoe.delta.settings.components.PresetSheet
import dev.shadoe.delta.settings.components.SecurityTypeField
import dev.shadoe.delta.settings.components.SsidField
import kotlinx.coroutines.launch

private fun openSystemSettings(context: Context, isBigScreen: Boolean = false) {
  context.startActivity(
    Intent("com.android.settings.WIFI_TETHER_SETTINGS").apply {
      if (isBigScreen) {
        addFlags(
          Intent.FLAG_ACTIVITY_LAUNCH_ADJACENT or Intent.FLAG_ACTIVITY_NEW_TASK
        )
      }
    }
  )
}

@Composable
fun SettingsScreen(
  modifier: Modifier = Modifier,
  onNavigateUp: (() -> Unit)?,
  vm: SettingsViewModel = viewModel(),
) {
  val focusManager = LocalFocusManager.current
  val isBigScreen = LocalConfiguration.current.screenWidthDp >= 700
  @OptIn(ExperimentalMaterial3Api::class)
  val sheetState = rememberModalBottomSheetState()
  val scope = rememberCoroutineScope()
  val snackbarHostState = remember { SnackbarHostState() }

  val config by vm.config.collectAsState()
  val status by vm.status.collectAsState()
  val results by vm.results.collectAsState()
  val presets by vm.presets.collectAsState(listOf())

  val passphraseEmptyWarningText =
    stringResource(R.string.passphrase_empty_warning)
  val failedToSaveText = stringResource(R.string.save_changes_failed_warning)

  var isAdvancedSettingsEnabled by remember { mutableStateOf(false) }
  var isPresetListShown by remember { mutableStateOf(false) }

  @OptIn(ExperimentalMaterial3Api::class)
  val appBarScrollBehavior =
    TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

  Scaffold(
    topBar = {
      @OptIn(ExperimentalMaterial3Api::class)
      LargeTopAppBar(
        title = { Text(text = stringResource(R.string.settings)) },
        navigationIcon = {
          if (onNavigateUp != null) {
            IconButton(onClick = onNavigateUp) {
              Icon(
                imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                contentDescription = stringResource(R.string.back_button),
              )
            }
          }
        },
        actions = {
          val context = LocalContext.current
          IconButton(onClick = { openSystemSettings(context, isBigScreen) }) {
            Icon(
              imageVector = Icons.AutoMirrored.Rounded.OpenInNew,
              contentDescription = stringResource(R.string.open_system_settings),
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
      if (Build.MANUFACTURER == "samsung") {
        item {
          Box(
            modifier =
              Modifier.background(
                color = MaterialTheme.colorScheme.tertiaryContainer,
                shape = RoundedCornerShape(12.dp),
              )
          ) {
            Row(
              modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
              verticalAlignment = Alignment.CenterVertically,
            ) {
              Icon(
                imageVector = Icons.Outlined.Warning,
                contentDescription = stringResource(R.string.warning_icon),
              )
              Column(modifier = Modifier.padding(start = 16.dp)) {
                val context = LocalContext.current
                Text(
                  text = stringResource(R.string.settings_warn_samsung),
                  color = MaterialTheme.colorScheme.onTertiaryContainer,
                )
                Button(
                  onClick = { openSystemSettings(context, isBigScreen) },
                  modifier = Modifier.padding(top = 8.dp),
                ) {
                  Icon(
                    imageVector = Icons.AutoMirrored.Rounded.OpenInNew,
                    contentDescription = stringResource(R.string.open_icon),
                  )
                  Text(
                    text = stringResource(R.string.open_system_settings),
                    modifier = Modifier.padding(start = 8.dp),
                  )
                }
              }
            }
          }
        }
      }
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
          currentResult = results.ssidResult,
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
            currentResult = results.passphraseResult,
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
            text = stringResource(R.string.advanced_settings_label),
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
                stringResource(R.string.collapse_icon)
              } else {
                stringResource(R.string.expand_icon)
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
        item {
          PresetField(
            onShowPresets = { isPresetListShown = true },
            onSaveConfig = { vm.saveConfigAsPreset() },
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
                onNavigateUp?.invoke()
              }
            }
        ) {
          Text(text = stringResource(R.string.save_button))
        }
      }
    }
  }

  if (isPresetListShown) {
    @OptIn(ExperimentalMaterial3Api::class)
    PresetSheet(
      sheetState = sheetState,
      presets = presets,
      onDismissRequest = { isPresetListShown = false },
      timestampToStringConverter = { vm.convertUnixTSToTime(it) },
      applyPreset = {
        vm.applyPreset(it)
        isPresetListShown = false
      },
      deletePreset = { vm.deletePreset(it) },
    )
  }
}
