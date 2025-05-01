package dev.shadoe.delta.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.shadoe.delta.api.SoftApConfiguration
import dev.shadoe.delta.api.SoftApSecurityType
import dev.shadoe.delta.api.SoftApSpeedType
import dev.shadoe.delta.data.FlagsRepository
import dev.shadoe.delta.data.database.dao.PresetDao
import dev.shadoe.delta.data.database.models.Preset
import dev.shadoe.delta.data.softap.SoftApControlRepository
import dev.shadoe.delta.data.softap.SoftApStateStore
import dev.shadoe.delta.data.softap.validators.PassphraseValidator
import dev.shadoe.delta.data.softap.validators.SsidValidator
import javax.inject.Inject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant

private data class SettingsFlags(val insecureReceiverEnabled: Boolean)

@HiltViewModel
class SettingsViewModel
@Inject
constructor(
  private val softApControlRepository: SoftApControlRepository,
  private val softApStateStore: SoftApStateStore,
  private val flagsRepository: FlagsRepository,
  private val presetDao: PresetDao,
) : ViewModel() {
  private val _config = MutableStateFlow(softApStateStore.config.value)
  private val _results = MutableStateFlow(UpdateResults())
  private val _flags =
    MutableStateFlow(SettingsFlags(insecureReceiverEnabled = false))

  val status = softApStateStore.status
  val config = _config.asStateFlow()
  val results = _results.asStateFlow()
  val presets = presetDao.observePresets()

  init {
    // TODO: instead of auto-updating, show a message in UI asking if user
    //       wants to reload config
    viewModelScope.launch {
      softApStateStore.config.collect {
        if (_config.value == it) return@collect
        _config.value = it
      }
    }
    viewModelScope.launch {
      updateTaskerIntegrationStatus(flagsRepository.isInsecureReceiverEnabled())
    }
  }

  @OptIn(ExperimentalCoroutinesApi::class)
  val taskerIntegrationStatus = _flags.mapLatest { it.insecureReceiverEnabled }

  fun convertUnixTSToTime(timestamp: Long) =
    Instant.fromEpochMilliseconds(timestamp).toString()

  fun updateSsid(ssid: String) =
    SsidValidator.validate(ssid)
      .let { res -> _results.update { it.copy(ssidResult = res) } }
      .also { _config.update { it.copy(ssid = ssid) } }

  fun updateSecurityType(securityType: Int) {
    val shouldSwitchBackTo5G =
      securityType != SoftApSecurityType.SECURITY_TYPE_WPA3_SAE &&
        _config.value.speedType == SoftApSpeedType.BAND_6GHZ
    _config.value =
      _config.value.copy(
        speedType =
          if (shouldSwitchBackTo5G) {
            SoftApSpeedType.BAND_5GHZ
          } else {
            _config.value.speedType
          },
        securityType = securityType,
      )
  }

  fun updatePassphrase(passphrase: String) =
    PassphraseValidator.validate(
        passphrase,
        securityType = config.value.securityType,
      )
      .let { res -> _results.update { it.copy(passphraseResult = res) } }
      .also { _config.update { it.copy(passphrase = passphrase) } }

  fun updateAutoShutdown(isAutoShutdownEnabled: Boolean) {
    _config.value =
      _config.value.copy(isAutoShutdownEnabled = isAutoShutdownEnabled)
  }

  fun updateSpeedType(speedType: Int) {
    val shouldSwitchToSAE =
      speedType == SoftApSpeedType.BAND_6GHZ &&
        _config.value.securityType != SoftApSecurityType.SECURITY_TYPE_WPA3_SAE
    _config.value =
      _config.value.copy(
        speedType = speedType,
        securityType =
          if (shouldSwitchToSAE) {
            SoftApSecurityType.SECURITY_TYPE_WPA3_SAE
          } else {
            _config.value.securityType
          },
      )
  }

  fun updateIsHidden(isHidden: Boolean) {
    _config.value = _config.value.copy(isHidden = isHidden)
  }

  fun updateMaxClientLimit(maxClient: Int) {
    _config.value = _config.value.copy(maxClientLimit = maxClient)
  }

  fun updateMacRandomizationSetting(macRandomizationSetting: Int) {
    _config.value =
      _config.value.copy(macRandomizationSetting = macRandomizationSetting)
  }

  fun updateAutoShutdownTimeout(autoShutdownTimeOut: Long) {
    _config.value =
      _config.value.copy(autoShutdownTimeout = autoShutdownTimeOut)
  }

  fun deletePreset(preset: Preset) =
    viewModelScope.launch { presetDao.delete(preset) }

  fun saveConfigAsPreset(name: String): Boolean {
    val canSave = results.value == UpdateResults()
    if (canSave) {
      viewModelScope.launch {
        _config.value
          .run {
            Preset(
              ssid = ssid,
              passphrase = passphrase,
              securityType = securityType,
              macRandomizationSetting = macRandomizationSetting,
              isHidden = isHidden,
              speedType = speedType,
              blockedDevices = blockedDevices,
              allowedClients = allowedClients,
              isAutoShutdownEnabled = isAutoShutdownEnabled,
              autoShutdownTimeout = autoShutdownTimeout,
              maxClientLimit = maxClientLimit,
              presetName = name,
              timestamp = Clock.System.now().toEpochMilliseconds(),
            )
          }
          .let { presetDao.insert(it) }
      }
    }
    return canSave
  }

  fun applyPreset(preset: Preset) {
    _results.update {
      UpdateResults(
        ssidResult =
          preset.ssid?.let { SsidValidator.validate(it) }
            ?: SsidValidator.Result.Success,
        passphraseResult =
          PassphraseValidator.validate(preset.passphrase, preset.securityType),
      )
    }
    _config.update {
      preset.run {
        SoftApConfiguration(
          ssid = ssid,
          passphrase = passphrase,
          securityType = securityType,
          macRandomizationSetting = macRandomizationSetting,
          isHidden = isHidden,
          speedType = speedType,
          blockedDevices = blockedDevices,
          allowedClients = allowedClients,
          isAutoShutdownEnabled = isAutoShutdownEnabled,
          autoShutdownTimeout = autoShutdownTimeout,
          maxClientLimit = maxClientLimit,
        )
      }
    }
  }

  fun updateTaskerIntegrationStatus(enabled: Boolean) {
    _flags.update { it.copy(insecureReceiverEnabled = enabled) }
  }

  // TODO: emit errors in UI
  fun commit(): Boolean {
    if (results.value != UpdateResults()) return false
    // Only happens when someone erases the passphrase but also selects
    // open security
    if (_config.value.passphrase.isEmpty()) {
      _config.value =
        _config.value.copy(
          passphrase = softApStateStore.config.value.passphrase
        )
    }
    softApControlRepository.updateSoftApConfiguration(_config.value)
    viewModelScope.launch {
      flagsRepository.setInsecureReceiverStatus(
        _flags.value.insecureReceiverEnabled
      )
    }
    return true
  }
}
