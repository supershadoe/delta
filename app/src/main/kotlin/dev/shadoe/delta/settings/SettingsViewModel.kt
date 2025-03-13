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
import kotlinx.coroutines.launch

@HiltViewModel
class SettingsViewModel
@Inject
constructor(private val softApRepository: SoftApRepository) : ViewModel() {
  companion object {
    private const val SSID_FIELD = 1 shl 1
    @Suppress("unused") private const val SECURITY_TYPE_FIELD = 1 shl 2
    private const val PASSPHRASE_FIELD = 1 shl 3
    @Suppress("unused") private const val AUTO_SHUTDOWN_FIELD = 1 shl 4
    @Suppress("unused") private const val SPEED_TYPE_FIELD = 1 shl 5
  }

  private var errorFlag = 0

  val status = softApRepository.status

  private val _config = MutableStateFlow(softApRepository.config.value)
  val config = _config.asStateFlow()

  init {
    viewModelScope.launch {
      softApRepository.config.collect {
        if (_config.value == it) return@collect
        _config.value = it
      }
    }
  }

  fun updateSsid(ssid: String) =
    when (SsidValidator.validate(ssid)) {
      is SsidValidator.Result.Success -> {
        _config.value = _config.value.copy(ssid = ssid)
        errorFlag = errorFlag and SSID_FIELD.inv()
      }
      is SsidValidator.Result.SsidTooShort,
      is SsidValidator.Result.SsidTooLong -> errorFlag = errorFlag or SSID_FIELD
    }

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
    when (
      PassphraseValidator.validate(
        passphrase,
        securityType = config.value.securityType,
      )
    ) {
      is PassphraseValidator.Result.Success -> {
        _config.value = _config.value.copy(passphrase = passphrase)
        errorFlag = errorFlag and PASSPHRASE_FIELD.inv()
      }
      is PassphraseValidator.Result.PskTooShort,
      is PassphraseValidator.Result.PskTooLong ->
        errorFlag = errorFlag or PASSPHRASE_FIELD
    }

  fun updateAutoShutdown(isAutoShutdownEnabled: Boolean) {
    _config.value =
      _config.value.copy(isAutoShutdownEnabled = isAutoShutdownEnabled)
  }

  fun updateHiddenHotspot(isHotspotHidden: Boolean) {
    _config.value = _config.value.copy(isHidden = isHotspotHidden)
  }

  fun updateMaxClientLimit(maxClient: Int) {
    _config.value = _config.value.copy(maxClientLimit = maxClient)
  }

  fun updateMACRandomizationType(MACRandomizationType: Int) {
    _config.value =
      _config.value.copy(macRandomizationSetting = MACRandomizationType)
  }

  fun updateAutoShutdownTimeout(autoShutdownTimeOut: Long) {
    _config.value =
      _config.value.copy(autoShutdownTimeout = autoShutdownTimeOut)
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

  // TODO: emit errors in UI
  fun commit(): Boolean {
    if (errorFlag != 0) return false
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
