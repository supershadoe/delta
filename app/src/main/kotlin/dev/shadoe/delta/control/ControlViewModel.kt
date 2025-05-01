package dev.shadoe.delta.control

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.shadoe.delta.api.ACLDevice
import dev.shadoe.delta.data.softap.SoftApBlocklistManager
import dev.shadoe.delta.data.softap.SoftApStateStore
import javax.inject.Inject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.mapLatest

@HiltViewModel
class ControlViewModel
@Inject
constructor(
  state: SoftApStateStore,
  private val softApBlocklistManager: SoftApBlocklistManager,
) : ViewModel() {
  @OptIn(ExperimentalCoroutinesApi::class)
  val enabledState = state.status.mapLatest { it.enabledState }

  @OptIn(ExperimentalCoroutinesApi::class)
  val connectedClients = state.status.mapLatest { it.tetheredClients }

  @OptIn(ExperimentalCoroutinesApi::class)
  val supportsBlocklist =
    state.status.mapLatest { it.capabilities.clientForceDisconnectSupported }

  val blockedClients = softApBlocklistManager.blockedClients

  fun blockDevices(devices: Iterable<ACLDevice>) =
    softApBlocklistManager.blockDevices(devices)

  fun unblockDevices(devices: Iterable<ACLDevice>) =
    softApBlocklistManager.unblockDevices(devices)
}
