package dev.shadoe.delta.data

import android.net.wifi.IWifiManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.shadoe.hotspotapi.exceptions.BinderAcquisitionException
import rikka.shizuku.ShizukuBinderWrapper
import rikka.shizuku.SystemServiceHelper

@Module
@InstallIn(SingletonComponent::class)
object WifiModule {
    @WifiSystemService
    @Provides
    fun provideWifiManager(): IWifiManager {
        return SystemServiceHelper
            .getSystemService("wifi")
            ?.let { ShizukuBinderWrapper(it) }
            ?.let { IWifiManager.Stub.asInterface(it) }
            ?: throw BinderAcquisitionException("Unable to get IWifiManager")
    }
}
