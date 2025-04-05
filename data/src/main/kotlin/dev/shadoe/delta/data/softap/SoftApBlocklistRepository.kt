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
    softApStateRepository.config.mapLatest {
      macAddressCacheRepository
        .getHostnamesFromCache(it.blockedDevices.map { it.macAddress })
        .map {
          ACLDevice(
            hostname = it.hostname,
            macAddress = MacAddress(it.macAddress),
          )
        }
    }

  fun blockDevice(device: ACLDevice) {
    softApStateRepository.apply {
      config.value.let {
        softApControlRepository.updateSoftApConfiguration(
          it.copy(blockedDevices = it.blockedDevices + device.macAddress)
        )
      }
    }
  }

  fun unblockDevice(device: ACLDevice) {
    softApStateRepository.apply {
      config.value.let {
        softApControlRepository.updateSoftApConfiguration(
          it.copy(blockedDevices = it.blockedDevices - device.macAddress)
        )
      }
    }
  }
}
