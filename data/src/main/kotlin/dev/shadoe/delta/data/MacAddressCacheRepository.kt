package dev.shadoe.delta.data

import dev.shadoe.delta.data.database.dao.HostInfoDao
import dev.shadoe.delta.data.database.models.HostInfo
import javax.inject.Inject

class MacAddressCacheRepository
@Inject
constructor(private val hostInfoDao: HostInfoDao) {
  internal suspend fun updateHostInfoInCache(
    clients: List<Pair<String, String>>
  ) {
    hostInfoDao.addHostInfo(
      *clients
        .map { it -> HostInfo(macAddress = it.first, hostname = it.second) }
        .toTypedArray()
    )
  }

  suspend fun getHostnamesFromCache(macAddressList: List<String>) =
    hostInfoDao.resolveMacAddressesToHostNames(macAddressList)

  suspend fun debugDumpCache() = hostInfoDao.dump()
}
