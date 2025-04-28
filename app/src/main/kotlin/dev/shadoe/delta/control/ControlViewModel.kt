package dev.shadoe.delta.control

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.shadoe.delta.api.ACLDevice
import dev.shadoe.delta.data.softap.SoftApBlocklistRepository
import dev.shadoe.delta.data.softap.SoftApStateRepository
import javax.inject.Inject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.mapLatest

@HiltViewModel
class ControlViewModel
@Inject
constructor(
  state: SoftApStateRepository,
  private val softApBlocklistRepository: SoftApBlocklistRepository,
) : ViewModel() {
  @OptIn(ExperimentalCoroutinesApi::class)
  val enabledState = state.status.mapLatest { it.enabledState }

  @OptIn(ExperimentalCoroutinesApi::class)
  val connectedClients = state.status.mapLatest { it.tetheredClients }

  @OptIn(ExperimentalCoroutinesApi::class)
  val supportsBlocklist =
    state.status.mapLatest { it.capabilities.clientForceDisconnectSupported }

  val blockedClients = softApBlocklistRepository.blockedClients

  fun blockDevice(device: ACLDevice) =
    softApBlocklistRepository.blockDevice(device)

  fun unblockDevices(devices: Set<ACLDevice>) =
    softApBlocklistRepository.unblockDevices(devices)
}
