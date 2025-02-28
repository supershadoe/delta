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
class MacAddressCacheModule {
    @MacAddressCache
    @Provides
    fun provideMacAddressCache(
        @ApplicationContext applicationContext: Context,
    ) = PreferenceDataStoreFactory.create {
        File(
            applicationContext.filesDir,
            "datastore/mac_address_cache.preferences_pb",
        )
    }
}
