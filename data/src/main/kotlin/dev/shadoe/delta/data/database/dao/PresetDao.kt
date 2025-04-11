package dev.shadoe.delta.data.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import dev.shadoe.delta.data.database.models.Preset

@Dao
interface PresetDao {
  @Query("SELECT * FROM Preset") suspend fun getAll(): List<Preset>

  @Insert suspend fun insert(preset: Preset)

  @Delete suspend fun delete(preset: Preset)
}
