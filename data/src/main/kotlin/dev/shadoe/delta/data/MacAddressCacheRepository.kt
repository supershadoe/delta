package dev.shadoe.delta.data

import dev.shadoe.delta.api.MacAddress
import dev.shadoe.delta.api.TetheredClient
import dev.shadoe.delta.data.database.dao.HostInfoDao
import dev.shadoe.delta.data.database.models.HostInfo
import javax.inject.Inject

class MacAddressCacheRepository
@Inject
constructor(private val hostInfoDao: HostInfoDao) {
  internal suspend fun updateHostInfoInCache(clients: List<TetheredClient>) {
    hostInfoDao.addHostInfo(
      *clients
        .filter { it.hostname != null }
        .map { HostInfo(macAddress = it.macAddress, hostname = it.hostname!!) }
        .toTypedArray()
    )
  }

  suspend fun getHostnamesFromCache(macAddressList: List<MacAddress>) =
    hostInfoDao.resolveMacAddressesToHostNames(macAddressList).associate {
      it.macAddress to it.hostname
    }

  suspend fun debugDumpCache() = hostInfoDao.dump()
}
