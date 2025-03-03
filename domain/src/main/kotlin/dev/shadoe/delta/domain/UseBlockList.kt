package dev.shadoe.delta.domain

import dev.shadoe.delta.api.ACLDevice
import javax.inject.Inject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.mapLatest

class UseBlockList
@Inject
constructor(
  private val getHotspotConfig: GetHotspotConfig,
  private val editHotspotConfig: EditHotspotConfig,
) {
  @OptIn(ExperimentalCoroutinesApi::class)
  fun getBlockedClientsFlow(): Flow<List<ACLDevice>> =
    getHotspotConfig().mapLatest { it.blockedDevices }

  fun blockClient(device: ACLDevice) =
    getHotspotConfig().value.let {
      editHotspotConfig(it.copy(blockedDevices = it.blockedDevices + device))
    }

  fun unblockClient(device: ACLDevice) =
    getHotspotConfig().value.let {
      editHotspotConfig(it.copy(blockedDevices = it.blockedDevices - device))
    }
}
