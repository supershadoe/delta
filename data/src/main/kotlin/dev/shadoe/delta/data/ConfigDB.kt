package dev.shadoe.delta.data

import androidx.room.Database
import androidx.room.RoomDatabase
import dev.shadoe.delta.data.dao.HostInfoDao
import dev.shadoe.delta.data.dao.PresetDao
import dev.shadoe.delta.data.models.HostInfo
import dev.shadoe.delta.data.models.Preset

@Database(entities = [HostInfo::class, Preset::class], version = 1)
abstract class ConfigDB : RoomDatabase() {
  abstract fun hostInfoDao(): HostInfoDao

  abstract fun presetDao(): PresetDao
}
