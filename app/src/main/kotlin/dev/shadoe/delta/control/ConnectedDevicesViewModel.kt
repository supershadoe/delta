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
class ConnectedDevicesViewModel
@Inject
constructor(
  softApStateRepository: SoftApStateRepository,
  private val softApBlocklistRepository: SoftApBlocklistRepository,
) : ViewModel() {
  @OptIn(ExperimentalCoroutinesApi::class)
  val connectedClients =
    softApStateRepository.status.mapLatest { it.tetheredClients }

  fun blockDevice(device: ACLDevice) {
    softApBlocklistRepository.blockDevice(device)
  }
}
