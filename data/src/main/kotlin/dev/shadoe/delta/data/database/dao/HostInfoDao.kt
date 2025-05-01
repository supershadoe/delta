package dev.shadoe.delta.data.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.TypeConverters
import androidx.room.Upsert
import dev.shadoe.delta.api.MacAddress
import dev.shadoe.delta.data.database.converters.MacAddressConverter
import dev.shadoe.delta.data.database.models.HostInfo

@TypeConverters(MacAddressConverter::class)
@Dao
interface HostInfoDao {
  @Query("SELECT * FROM HostInfo WHERE macAddress IN (:macAddress)")
  suspend fun resolveMacAddressesToHostNames(
    macAddress: List<MacAddress>
  ): List<HostInfo>

  @Query("SELECT * FROM HostInfo") suspend fun dump(): List<HostInfo>

  @Upsert suspend fun addHostInfo(vararg hostInfo: HostInfo)
}
