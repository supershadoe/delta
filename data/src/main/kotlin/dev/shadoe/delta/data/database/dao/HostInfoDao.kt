package dev.shadoe.delta.data.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import dev.shadoe.delta.data.database.models.HostInfo

@Dao
interface HostInfoDao {
  @Query("SELECT * FROM HostInfo WHERE macAddress IN (:macAddress)")
  suspend fun resolveMacAddressesToHostNames(
    macAddress: List<String>
  ): List<HostInfo>

  @Query("SELECT * FROM HostInfo") suspend fun dump(): List<HostInfo>

  @Upsert suspend fun addHostInfo(vararg hostInfo: HostInfo)
}
