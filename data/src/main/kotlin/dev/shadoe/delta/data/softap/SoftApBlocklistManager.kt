package dev.shadoe.delta.data.softap

import dev.shadoe.delta.api.ACLDevice
import dev.shadoe.delta.data.MacAddressCacheRepository
import javax.inject.Inject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.mapLatest

class SoftApBlocklistManager
@Inject
constructor(
  private val macAddressCacheRepository: MacAddressCacheRepository,
  private val softApController: SoftApController,
  private val softApStateStore: SoftApStateStore,
) {
  @OptIn(ExperimentalCoroutinesApi::class)
  val blockedClients =
    softApStateStore.config.mapLatest { c ->
      macAddressCacheRepository.getHostnamesFromCache(c.blockedDevices).map {
        ACLDevice(hostname = it.hostname, macAddress = it.macAddress)
      }
    }

  fun blockDevices(devices: Iterable<ACLDevice>) {
    softApStateStore.config.value.let { c ->
      softApController.updateSoftApConfiguration(
        c.copy(
          blockedDevices = c.blockedDevices.plus(devices.map { it.macAddress })
        )
      )
    }
  }

  fun unblockDevices(devices: Iterable<ACLDevice>) {
    softApStateStore.config.value.let { c ->
      softApController.updateSoftApConfiguration(
        c.copy(
          blockedDevices = c.blockedDevices.minus(devices.map { it.macAddress })
        )
      )
    }
  }
}
