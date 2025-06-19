package dev.shadoe.delta.data.modules

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dev.shadoe.delta.data.database.ConfigDB
import dev.shadoe.delta.data.qualifiers.ConfigDatabase
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DaoModule {
  const val DB_NAME = "config_database"

  @Singleton
  @ConfigDatabase
  @Provides
  fun provideConfigDatabase(@ApplicationContext applicationContext: Context) =
    Room.databaseBuilder(applicationContext, ConfigDB::class.java, DB_NAME)
      .build()

  @Singleton
  @Provides
  fun provideFlagsDao(@ConfigDatabase configDB: ConfigDB) = configDB.flagsDao()

  @Singleton
  @Provides
  fun provideHostInfoDao(@ConfigDatabase configDB: ConfigDB) =
    configDB.hostInfoDao()

  @Singleton
  @Provides
  fun providePresetDao(@ConfigDatabase configDB: ConfigDB) =
    configDB.presetDao()
}
