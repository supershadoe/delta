package dev.shadoe.delta.data.database

import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.RoomDatabase
import dev.shadoe.delta.data.database.dao.FlagsDao
import dev.shadoe.delta.data.database.dao.HostInfoDao
import dev.shadoe.delta.data.database.dao.PresetDao
import dev.shadoe.delta.data.database.models.Flag
import dev.shadoe.delta.data.database.models.HostInfo
import dev.shadoe.delta.data.database.models.Preset

@Database(
  entities = [Flag::class, HostInfo::class, Preset::class],
  autoMigrations = [
    AutoMigration(from = 1, to = 2),
    AutoMigration(from = 2, to = 3),
  ],
  version = 3,
)
abstract class ConfigDB : RoomDatabase() {
  abstract fun flagsDao(): FlagsDao

  abstract fun hostInfoDao(): HostInfoDao

  abstract fun presetDao(): PresetDao
}
