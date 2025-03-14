package dev.shadoe.delta.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.shadoe.delta.api.SoftApSecurityType
import dev.shadoe.delta.api.SoftApSpeedType
import dev.shadoe.delta.data.softap.SoftApRepository
import dev.shadoe.delta.data.softap.validators.PassphraseValidator
import dev.shadoe.delta.data.softap.validators.SsidValidator
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class SettingsViewModel
@Inject
constructor(private val softApRepository: SoftApRepository) : ViewModel() {
  private val _config = MutableStateFlow(softApRepository.config.value)
  private val _results = MutableStateFlow(UpdateResults())

  val status = softApRepository.status
  val config = _config.asStateFlow()
  val results = _results.asStateFlow()

  init {
    viewModelScope.launch {
      softApRepository.config.collect {
        if (_config.value == it) return@collect
        _config.value = it
      }
    }
  }

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

  // TODO: emit errors in UI
  fun commit(): Boolean {
    if (results.value != UpdateResults()) return false
    // Only happens when someone erases the passphrase but also selects
    // open security
    if (_config.value.passphrase.isEmpty()) {
      _config.value =
        _config.value.copy(
          passphrase = softApRepository.config.value.passphrase
        )
    }
    softApRepository.config.value = _config.value
    return true
  }
}
