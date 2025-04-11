package dev.shadoe.delta.data.modules

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.shadoe.delta.data.database.ConfigDB
import dev.shadoe.delta.data.qualifiers.ConfigDatabase

@Module
@InstallIn(SingletonComponent::class)
object DaoModule {
  @Provides
  fun provideFlagsDao(@ConfigDatabase configDB: ConfigDB) = configDB.flagsDao()

  @Provides
  fun provideHostInfoDao(@ConfigDatabase configDB: ConfigDB) =
    configDB.hostInfoDao()

  @Provides
  fun providePresetDao(@ConfigDatabase configDB: ConfigDB) =
    configDB.presetDao()
}
