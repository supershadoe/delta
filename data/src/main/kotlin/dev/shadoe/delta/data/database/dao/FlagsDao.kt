package dev.shadoe.delta.data.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import dev.shadoe.delta.data.database.models.Flag

@Dao
interface FlagsDao {
  @Query("SELECT value FROM Flag WHERE flag = :flag LIMIT 1")
  suspend fun getFlag(flag: Int): Boolean?

  @Upsert suspend fun setFlag(flag: Flag)
}
