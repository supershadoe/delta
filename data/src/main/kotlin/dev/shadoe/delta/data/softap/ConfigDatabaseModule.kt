package dev.shadoe.delta.data.softap

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dev.shadoe.delta.data.ConfigDB

@Module
@InstallIn(SingletonComponent::class)
class ConfigDatabaseModule {
  @ConfigDatabase
  @Provides
  fun provideConfigDatabase(@ApplicationContext applicationContext: Context) =
    Room.databaseBuilder(
        applicationContext,
        ConfigDB::class.java,
        "config_database",
      )
      .build()
}
