package dev.shadoe.delta.control

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.shadoe.delta.api.ACLDevice
import dev.shadoe.delta.api.SoftApConfiguration
import dev.shadoe.delta.data.database.dao.PresetDao
import dev.shadoe.delta.data.database.models.Preset
import dev.shadoe.delta.data.softap.SoftApBlocklistManager
import dev.shadoe.delta.data.softap.SoftApController
import dev.shadoe.delta.data.softap.SoftApStateStore
import javax.inject.Inject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.mapLatest

@HiltViewModel
class ControlViewModel
@Inject
constructor(
  state: SoftApStateStore,
  presetDao: PresetDao,
  private val softApBlocklistManager: SoftApBlocklistManager,
  private val softApController: SoftApController,
) : ViewModel() {
  @OptIn(ExperimentalCoroutinesApi::class)
  val enabledState = state.status.mapLatest { it.enabledState }

  @OptIn(ExperimentalCoroutinesApi::class)
  val connectedClients = state.status.mapLatest { it.tetheredClients }

  @OptIn(ExperimentalCoroutinesApi::class)
  val supportsBlocklist =
    state.status.mapLatest { it.capabilities.clientForceDisconnectSupported }

  val presets = presetDao.observePresets()

  val blockedClients = softApBlocklistManager.blockedClients

  fun blockDevices(devices: Iterable<ACLDevice>) =
    softApBlocklistManager.blockDevices(devices)

  fun unblockDevices(devices: Iterable<ACLDevice>) =
    softApBlocklistManager.unblockDevices(devices)

  fun applyPreset(preset: Preset) =
    softApController.updateSoftApConfiguration(
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
    )
}
