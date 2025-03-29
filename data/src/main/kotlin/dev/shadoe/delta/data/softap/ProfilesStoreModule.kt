package dev.shadoe.delta.data.softap

import android.content.Context
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import java.io.File

@Module
@InstallIn(SingletonComponent::class)
class ProfilesStoreModule {
  @ProfilesStore
  @Provides
  fun provideProfilesStore(@ApplicationContext applicationContext: Context) =
    PreferenceDataStoreFactory.create {
      File(
        applicationContext.filesDir,
        "datastore/profiles_store.preferences_pb",
      )
    }
}
