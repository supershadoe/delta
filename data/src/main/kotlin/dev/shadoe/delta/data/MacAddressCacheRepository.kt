package dev.shadoe.delta.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import dev.shadoe.delta.api.ConfigFlag
import dev.shadoe.delta.data.models.Flag
import dev.shadoe.delta.data.models.HostInfo
import dev.shadoe.delta.data.softap.ConfigDatabase
import dev.shadoe.delta.data.softap.FlagsRepository
import dev.shadoe.delta.data.softap.MacAddressCache
import javax.inject.Inject
import kotlin.collections.iterator
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class MacAddressCacheRepository
@Inject
constructor(
  @MacAddressCache private val persistedMacAddressCache: DataStore<Preferences>,
  @ConfigDatabase private val configDB: ConfigDB,
  private val flagsRepository: FlagsRepository,
) {
  private val hostInfoDao = configDB.hostInfoDao()
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
    configDB.hostInfoDao().addHostInfo(*records.toTypedArray())
    configDB
      .flagsDao()
      .setFlag(Flag(flag = ConfigFlag.USES_ROOM_DB.ordinal, value = true))
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
}
