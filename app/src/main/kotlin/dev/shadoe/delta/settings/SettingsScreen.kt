package dev.shadoe.delta.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material.icons.rounded.KeyboardArrowUp
import androidx.compose.material.icons.rounded.Link
import androidx.compose.material.icons.rounded.NetworkWifi
import androidx.compose.material.icons.rounded.Password
import androidx.compose.material.icons.rounded.SettingsPower
import androidx.compose.material.icons.rounded.Shuffle
import androidx.compose.material.icons.rounded.Timer
import androidx.compose.material.icons.rounded.Wifi
import androidx.compose.material.icons.rounded.WifiFind
import androidx.compose.material.icons.rounded.WifiPassword
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Switch
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
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import dev.shadoe.delta.R
import dev.shadoe.delta.api.AutoShutdownType
import dev.shadoe.delta.api.AutoShutdownType.getResOfAutoShutdownType
import dev.shadoe.delta.api.SoftApSecurityType
import dev.shadoe.delta.api.SoftApSecurityType.getResOfSecurityType
import dev.shadoe.delta.api.SoftApSpeedType.getResOfSpeedType
import dev.shadoe.delta.common.LocalNavController
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

  var isAdvancedSettingsEnabled by remember { mutableStateOf(false) }
  var sliderState by remember { mutableStateOf(config.maxClientLimit) }
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
      )
    },
    snackbarHost = { SnackbarHost(snackbarHostState) },
  ) { scaffoldPadding ->
    LazyColumn(
      modifier =
        Modifier
          .fillMaxSize()
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
        SSIDField(
          ssid = config.ssid ?: "",
          onSSIDChange = { vm.updateSsid(it) },
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
        SpeedTypeField(
          speedType = config.speedType,
          supportedSpeedTypes = status.capabilities.supportedFrequencyBands,
          onSpeedTypeChange = { vm.updateSpeedType(it) },
        )
      }

      item {
        Row(
          modifier =
            Modifier
              .fillMaxWidth()
              .clickable {
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
            onHiddenHotspotChange = { vm.updateHiddenHotspot(it) },
          )
        }
        item {
          MaxClientLimitField(
            maxClient = status.capabilities.maxSupportedClients,
            onMaxClientChange = { vm.updateMaxClientLimit(it) },
            allowedLimit = sliderState
          )
        }

        item{
          MACRandomizationField(
            MACRandomizationType = config.macRandomizationSetting,
            onMACRandomizationTypeChange = { vm.updateMACRandomizationType(it) },
          )
        }

        item {
//
          AutoShutDownTimeOutField(
            autoShutDownTimeOut = config.autoShutdownTimeout, supportedAutoShutdownType = status.capabilities.supportedAutoShutdownTypes,onAutoShutdownChange = { vm.updateAutoShutdownTimeout(it)},)
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
              vm.commit()
              navController?.navigateUp()
            }
        ) {
          Text(text = stringResource(R.string.save_button))
        }
      }
    }
  }
}

@Composable
private fun SSIDField(ssid: String, onSSIDChange: (String) -> Unit) {
  Row(
    verticalAlignment = Alignment.CenterVertically,
    modifier = Modifier.padding(vertical = 8.dp),
  ) {
    Icon(
      imageVector = Icons.Rounded.Wifi,
      contentDescription = stringResource(R.string.ssid_field_icon),
    )
    OutlinedTextField(
      value = ssid,
      onValueChange = { onSSIDChange(it) },
      singleLine = true,
      keyboardOptions =
        KeyboardOptions(
          capitalization = KeyboardCapitalization.None,
          autoCorrectEnabled = false,
          keyboardType = KeyboardType.Text,
          imeAction = ImeAction.Next,
        ),
      modifier = Modifier
        .padding(horizontal = 16.dp)
        .fillMaxWidth(),
      label = { Text(text = stringResource(R.string.ssid_field_label)) },
    )
  }
}

@Composable
private fun SecurityTypeField(
  securityType: Int,
  supportedSecurityTypes: List<Int>,
  onSecurityTypeChange: (Int) -> Unit,
) {
  Row(
    verticalAlignment = Alignment.CenterVertically,
    modifier = Modifier.padding(vertical = 8.dp),
  ) {
    Icon(
      imageVector = Icons.Rounded.WifiPassword,
      contentDescription = stringResource(R.string.security_proto_field_icon),
    )
    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
      Text(
        text = stringResource(R.string.security_proto_field_label),
        modifier = Modifier.padding(start = 8.dp),
        style = MaterialTheme.typography.titleMedium,
      )
      LazyRow {
        items(supportedSecurityTypes.size) {
          FilterChip(
            selected = securityType == supportedSecurityTypes[it],
            onClick = { onSecurityTypeChange(supportedSecurityTypes[it]) },
            label = {
              Text(
                text =
                  stringResource(
                    getResOfSecurityType(supportedSecurityTypes[it])
                  )
              )
            },
            modifier = Modifier.padding(horizontal = 2.dp),
          )
        }
      }
    }
  }
}

@Composable
private fun PassphraseField(
  passphrase: String,
  onPassphraseChange: (String) -> Unit,
) {
  Row(
    verticalAlignment = Alignment.CenterVertically,
    modifier = Modifier.padding(vertical = 8.dp),
  ) {
    Icon(
      imageVector = Icons.Rounded.Password,
      contentDescription = stringResource(R.string.passphrase_field_icon),
    )
    OutlinedTextField(
      value = passphrase,
      onValueChange = { onPassphraseChange(it) },
      singleLine = true,
      keyboardOptions =
        KeyboardOptions(
          capitalization = KeyboardCapitalization.None,
          autoCorrectEnabled = false,
          keyboardType = KeyboardType.Password,
          imeAction = ImeAction.Done,
        ),
      modifier = Modifier
        .padding(horizontal = 16.dp)
        .fillMaxWidth(),
      label = { Text(text = stringResource(R.string.passphrase_field_label)) },
    )
  }
}

@Composable
private fun AutoShutdownField(
  isAutoShutdownEnabled: Boolean,
  onAutoShutdownChange: (Boolean) -> Unit,
) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .padding(vertical = 16.dp),
    verticalAlignment = Alignment.CenterVertically,
  ) {
    Icon(
      imageVector = Icons.Rounded.SettingsPower,
      contentDescription = stringResource(R.string.auto_shutdown_field_icon),
    )
    Column(
      modifier = Modifier
        .weight(1f)
        .padding(horizontal = 16.dp),
      horizontalAlignment = Alignment.Start,
    ) {
      Text(
        text = stringResource(R.string.auto_shutdown_field_title),
        style = MaterialTheme.typography.titleLarge,
      )
      Text(
        text = stringResource(R.string.auto_shutdown_field_desc),
        color = MaterialTheme.colorScheme.onSurfaceVariant,
      )
    }
    Switch(
      checked = isAutoShutdownEnabled,
      onCheckedChange = { onAutoShutdownChange(it) },
    )
  }
}

@Composable
private fun HiddenHotspotField(
  isHiddenHotspotEnabled: Boolean,
  onHiddenHotspotChange: (Boolean) -> Unit,
) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .padding(vertical = 16.dp),
    verticalAlignment = Alignment.CenterVertically,
  ) {
    Icon(
      imageVector = Icons.Rounded.WifiFind,
      contentDescription = stringResource(R.string.hidden_network_field_icon),
    )
    Column(
      modifier = Modifier
        .weight(1f)
        .padding(horizontal = 16.dp),
      horizontalAlignment = Alignment.Start,
    ) {
      Text(
        text = stringResource(R.string.hidden_network_field_label),
        style = MaterialTheme.typography.titleLarge,
      )
      Text(
        text = stringResource(R.string.hidden_network_field_desc),
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
      )
    }
    Switch(
      checked = isHiddenHotspotEnabled,
      onCheckedChange = { onHiddenHotspotChange(it) },
    )
  }
}

@Composable
private fun MaxClientLimitField(
  allowedLimit : Int,
  maxClient: Int,
  onMaxClientChange: (Int) -> Unit,
) {

  Row(
    modifier = Modifier
      .fillMaxWidth()
      .padding(vertical = 16.dp),
    verticalAlignment = Alignment.CenterVertically,
  ) {
    Icon(
      imageVector = Icons.Rounded.Link,
      contentDescription =
      stringResource(R.string.maximum_client_limit_field_icon),
    )
    Column(
      modifier = Modifier
        .weight(1f)
        .padding(horizontal = 16.dp),
      horizontalAlignment = Alignment.Start,
    ) {
      Text(
        text = stringResource(R.string.maximum_client_limit_field_label),
        style = MaterialTheme.typography.titleLarge,
      )
      var sliderState by remember { mutableStateOf(allowedLimit.toFloat()) }
      Slider(

        value = sliderState,
        onValueChange = {
          sliderState = it
          onMaxClientChange(it.toInt())
        },
        valueRange = 1f..maxClient.toFloat(),
        steps = maxClient.toInt() - 1,
        colors =
          SliderDefaults.colors(
            thumbColor = MaterialTheme.colorScheme.secondary,
            activeTrackColor = MaterialTheme.colorScheme.secondary,
            inactiveTrackColor = MaterialTheme.colorScheme.secondaryContainer,
          ),
      )
      Text(text = "${sliderState.toInt()} Clients", modifier = Modifier.align(alignment = Alignment.CenterHorizontally))
    }
  }
}

@Composable
private fun MACRandomizationField(MACRandomizationType : Int,onMACRandomizationTypeChange : (Int) -> Unit)
{
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .padding(vertical = 16.dp),
    verticalAlignment = Alignment.CenterVertically,
  ) {
    Icon(
      imageVector = Icons.Rounded.Shuffle,
      contentDescription = stringResource(R.string.mac_randomization_field_icon),
    )
    Column(
      modifier = Modifier
        .weight(1f)
        .padding(horizontal = 16.dp),
      horizontalAlignment = Alignment.Start,
    ) {
      Text(
        text = stringResource(R.string.mac_randomization_field_label),
        style = MaterialTheme.typography.titleLarge,
      )

      val supportedMACRandomizationType = listOf(stringResource(R.string.mac_randomization_none),
        stringResource(R.string.mac_randomization_persistent),
        stringResource(R.string.mac_randomization_non_persistent)
      )
      LazyRow {
        items(supportedMACRandomizationType.size) {
          FilterChip(
            selected = supportedMACRandomizationType[MACRandomizationType] == supportedMACRandomizationType[it],
            onClick = {  onMACRandomizationTypeChange(it)},
            label = {
              Text(
                text =
                supportedMACRandomizationType[it]
              )
            },
            modifier = Modifier.padding(horizontal = 2.dp),
          )
        }
      }
    }

  }
}

@Composable
private fun AutoShutDownTimeOutField(autoShutDownTimeOut : Long,supportedAutoShutdownType: List<Int>,onAutoShutdownChange:  (Long) -> Unit){
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .padding(vertical = 16.dp),
    verticalAlignment = Alignment.CenterVertically,
  ) {
    Icon(
      imageVector = Icons.Rounded.Timer,
      contentDescription = stringResource(R.string.auto_shutdown_timeout_field_icon),
    )
    Column(
      modifier = Modifier
        .weight(1f)
        .padding(horizontal = 16.dp),
      horizontalAlignment = Alignment.Start,
    ) {
      Text(
        text = stringResource(R.string.auto_shutdown_timeout_field_label),
        style = MaterialTheme.typography.titleLarge,
      )
      Text(
        text = stringResource(R.string.auto_shutdown_timeout_field_desc),
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
      )
      LazyRow {
        items(supportedAutoShutdownType.size) {
          FilterChip(
            selected = autoShutDownTimeOut.toInt() == supportedAutoShutdownType[it],
            onClick = { onAutoShutdownChange(supportedAutoShutdownType[it].toLong()) },
            label = {
              Text(
                text =
                stringResource(
                  getResOfAutoShutdownType(supportedAutoShutdownType[it])
                )
              )
            },
            modifier = Modifier.padding(horizontal = 2.dp),
          )
        }
      }
    }

  }
}

@Composable
private fun SpeedTypeField(
  speedType: Int,
  supportedSpeedTypes: List<Int>,
  onSpeedTypeChange: (Int) -> Unit,
) {
  Row(
    verticalAlignment = Alignment.CenterVertically,
    modifier = Modifier.padding(vertical = 8.dp),
  ) {
    Icon(
      imageVector = Icons.Rounded.NetworkWifi,
      contentDescription = stringResource(R.string.freq_band_field_icon),
    )
    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
      Text(
        text = stringResource(R.string.freq_band_field_label),
        modifier = Modifier.padding(start = 8.dp),
        style = MaterialTheme.typography.titleMedium,
      )
      LazyRow {
        items(supportedSpeedTypes.size) {
          FilterChip(
            selected = speedType == supportedSpeedTypes[it],
            onClick = { onSpeedTypeChange(supportedSpeedTypes[it]) },
            label = {
              Text(
                text =
                  stringResource(getResOfSpeedType(supportedSpeedTypes[it]))
              )
            },
            modifier = Modifier.padding(horizontal = 2.dp),
          )
        }
      }
    }
  }
}
