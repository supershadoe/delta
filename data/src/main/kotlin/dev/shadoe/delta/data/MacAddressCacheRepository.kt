package dev.shadoe.delta.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import dev.shadoe.delta.api.ConfigFlag
import dev.shadoe.delta.data.database.dao.FlagsDao
import dev.shadoe.delta.data.database.dao.HostInfoDao
import dev.shadoe.delta.data.database.models.Flag
import dev.shadoe.delta.data.database.models.HostInfo
import dev.shadoe.delta.data.qualifiers.MacAddressCache
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.collections.iterator
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@Singleton
class MacAddressCacheRepository
@Inject
constructor(
  @MacAddressCache private val persistedMacAddressCache: DataStore<Preferences>,
  private val flagsDao: FlagsDao,
  private val hostInfoDao: HostInfoDao,
  private val flagsRepository: FlagsRepository,
) {
  private val ioScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

  private suspend fun migrateFromDatastoreToRoom() {
    if (!flagsRepository.shouldMigrateToRoom()) return
    val data =
      persistedMacAddressCache.data.first().let {
        it
          .asMap()
          .asIterable()
          .filterIsInstance<Map.Entry<Preferences.Key<String>, String>>()
          .associate { it.key.name to it.value }
      }
    val records = mutableListOf<HostInfo>()
    for (entry in data) {
      records.add(HostInfo(macAddress = entry.key, hostname = entry.value))
    }
    hostInfoDao.addHostInfo(*records.toTypedArray())
    flagsDao.setFlag(Flag(flag = ConfigFlag.USES_ROOM_DB.ordinal, value = true))
  }

  init {
    ioScope.launch { migrateFromDatastoreToRoom() }
  }

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
