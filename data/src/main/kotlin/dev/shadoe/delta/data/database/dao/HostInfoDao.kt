package dev.shadoe.delta.data.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import dev.shadoe.delta.data.database.models.HostInfo
import kotlinx.coroutines.flow.Flow

@Dao
interface HostInfoDao {
  @Query("SELECT * FROM HostInfo WHERE macAddress IN (:macAddress)")
  suspend fun resolveMacAddressesToHostNames(
    macAddress: List<String>
  ): List<HostInfo>

  @Query("SELECT * FROM HostInfo") fun observeTable(): Flow<List<HostInfo>>

  @Upsert suspend fun addHostInfo(vararg hostInfo: HostInfo)

  @Delete suspend fun delete(hostInfo: HostInfo)
}
