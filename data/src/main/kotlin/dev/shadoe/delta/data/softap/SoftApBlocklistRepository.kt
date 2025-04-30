package dev.shadoe.delta.data.softap

import dev.shadoe.delta.api.ACLDevice
import dev.shadoe.delta.api.MacAddress
import dev.shadoe.delta.data.MacAddressCacheRepository
import javax.inject.Inject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.mapLatest

class SoftApBlocklistRepository
@Inject
constructor(
  private val macAddressCacheRepository: MacAddressCacheRepository,
  private val softApControlRepository: SoftApControlRepository,
  private val softApStateRepository: SoftApStateRepository,
) {
  @OptIn(ExperimentalCoroutinesApi::class)
  val blockedClients =
    softApStateRepository.config.mapLatest { c ->
      macAddressCacheRepository
        .getHostnamesFromCache(c.blockedDevices.map { it.macAddress })
        .map {
          ACLDevice(
            hostname = it.hostname,
            macAddress = MacAddress(it.macAddress),
          )
        }
    }

  fun blockDevices(devices: Iterable<ACLDevice>) {
    softApStateRepository.config.value.let { c ->
      softApControlRepository.updateSoftApConfiguration(
        c.copy(
          blockedDevices = c.blockedDevices.plus(devices.map { it.macAddress })
        )
      )
    }
  }

  fun unblockDevices(devices: Iterable<ACLDevice>) {
    softApStateRepository.config.value.let { c ->
      softApControlRepository.updateSoftApConfiguration(
        c.copy(
          blockedDevices = c.blockedDevices.minus(devices.map { it.macAddress })
        )
      )
    }
  }
}
