package dev.shadoe.delta.data.modules

import android.content.Context
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dev.shadoe.delta.data.qualifiers.MacAddressCache
import java.io.File
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object PersistenceModule {
  @Singleton
  @MacAddressCache
  @Provides
  fun provideMacAddressCache(@ApplicationContext applicationContext: Context) =
    PreferenceDataStoreFactory.create {
      File(
        applicationContext.filesDir,
        "datastore/mac_address_cache.preferences_pb",
      )
    }
}
