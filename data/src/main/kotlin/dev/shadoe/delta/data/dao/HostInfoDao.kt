package dev.shadoe.delta.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import dev.shadoe.delta.data.models.HostInfo

@Dao
interface HostInfoDao {
  @Query("SELECT * FROM HostInfo WHERE macAddress = :macAddress LIMIT 1")
  suspend fun getHostInfo(macAddress: String): HostInfo?

  @Query("SELECT * FROM HostInfo") suspend fun getAll(): List<HostInfo>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertAll(vararg hostInfo: HostInfo)

  @Delete suspend fun delete(hostInfo: HostInfo)
}
