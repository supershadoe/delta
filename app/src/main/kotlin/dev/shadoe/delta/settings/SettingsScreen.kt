package dev.shadoe.delta.settings

import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.OpenInNew
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarVisuals
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import dev.shadoe.delta.common.components.FoldableWrapper
import dev.shadoe.delta.settings.components.AutoShutDownTimeOutField
import dev.shadoe.delta.settings.components.AutoShutdownField
import dev.shadoe.delta.settings.components.FrequencyBandField
import dev.shadoe.delta.settings.components.HiddenHotspotField
import dev.shadoe.delta.settings.components.MacRandomizationField
import dev.shadoe.delta.settings.components.MaxClientLimitField
import dev.shadoe.delta.settings.components.PassphraseField
import dev.shadoe.delta.settings.components.PresetField
import dev.shadoe.delta.settings.components.PresetSaveDialog
import dev.shadoe.delta.settings.components.PresetSheet
import dev.shadoe.delta.settings.components.SecurityTypeField
import dev.shadoe.delta.settings.components.SoftApTileField
import dev.shadoe.delta.settings.components.SsidField
import dev.shadoe.delta.settings.components.TaskerIntegrationField
import dev.shadoe.delta.settings.components.TaskerIntegrationInfo

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
  onShowSnackbar: (SnackbarVisuals) -> Unit = {},
  vm: SettingsViewModel = viewModel(),
) {
  val focusManager = LocalFocusManager.current
  val isBigScreen = LocalConfiguration.current.screenWidthDp >= 700
  @OptIn(ExperimentalMaterial3Api::class)
  val sheetState = rememberModalBottomSheetState()

  val config by vm.config.collectAsState()
  val status by vm.status.collectAsState()
  val results by vm.results.collectAsState()
  val presets by vm.presets.collectAsState(listOf())
  val taskerIntegrationStatus by
    vm.taskerIntegrationStatus.collectAsState(false)

  val passphraseEmptyWarningSnackbar =
    object : SnackbarVisuals {
      override val message = stringResource(R.string.passphrase_empty_warning)
      override val withDismissAction = true
      override val actionLabel = null
      override val duration = SnackbarDuration.Short
    }

  val failedToSaveSnackbar =
    object : SnackbarVisuals {
      override val message =
        stringResource(R.string.save_changes_failed_warning)
      override val withDismissAction = true
      override val actionLabel = null
      override val duration = SnackbarDuration.Short
    }

  val savedSnackbar =
    object : SnackbarVisuals {
      override val message = stringResource(R.string.save_changes_succeeded)
      override val withDismissAction = true
      override val actionLabel = null
      override val duration = SnackbarDuration.Short
    }

  var isAdvancedSettingsEnabled by remember { mutableStateOf(false) }
  var shouldSavePreset by remember { mutableStateOf(false) }
  var isPresetListShown by remember { mutableStateOf(false) }
  var isTaskerInfoShown by remember { mutableStateOf(false) }

  LazyColumn(
    modifier =
      modifier.then(
        other =
          Modifier.fillMaxSize().padding(horizontal = 16.dp).pointerInput(
            Unit
          ) {
            detectTapGestures(onTap = { focusManager.clearFocus() })
          }
      )
  ) {
    if (Build.MANUFACTURER == "samsung") {
      item {
        Box(
          modifier =
            Modifier.padding(top = 16.dp)
              .background(
                color = MaterialTheme.colorScheme.tertiaryContainer,
                shape = RoundedCornerShape(12.dp),
              )
        ) {
          Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 16.dp),
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
        modifier = Modifier.padding(vertical = 16.dp),
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
      FoldableWrapper(
        text = stringResource(R.string.advanced_settings_label),
        foldableState = isAdvancedSettingsEnabled,
        onFoldableToggled = {
          isAdvancedSettingsEnabled = !isAdvancedSettingsEnabled
        },
      )
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
          onSaveConfig = { shouldSavePreset = true },
        )
      }
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        item { SoftApTileField(onShowSnackbar) }
      }
      item {
        TaskerIntegrationField(
          isTaskerIntegrationEnabled = taskerIntegrationStatus,
          onTaskerIntegrationChange = { vm.updateTaskerIntegrationStatus(it) },
          onShowTaskerIntegrationInfo = { isTaskerInfoShown = true },
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
              onShowSnackbar(passphraseEmptyWarningSnackbar)
              return@onClick
            }
            onShowSnackbar(
              if (vm.commit()) savedSnackbar else failedToSaveSnackbar
            )
          }
      ) {
        Text(text = stringResource(R.string.save_button))
      }
    }
  }

  if (shouldSavePreset) {
    PresetSaveDialog(
      onSave = {
        vm.saveConfigAsPreset(it)
        shouldSavePreset = false
      },
      onDismiss = { shouldSavePreset = false },
    )
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

  if (isTaskerInfoShown) {
    TaskerIntegrationInfo(onDismissDialog = { isTaskerInfoShown = false })
  }
}
