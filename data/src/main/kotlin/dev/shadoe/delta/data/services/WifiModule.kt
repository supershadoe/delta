package dev.shadoe.delta.data.services

import android.net.wifi.IWifiManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.shadoe.delta.data.exceptions.BinderAcquisitionException
import rikka.shizuku.ShizukuBinderWrapper
import rikka.shizuku.SystemServiceHelper
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object WifiModule {
  @Singleton
  @WifiSystemService
  @Provides
  fun provideWifiManager(): IWifiManager =
    SystemServiceHelper.getSystemService("wifi")
      ?.let { ShizukuBinderWrapper(it) }
      ?.let { IWifiManager.Stub.asInterface(it) }
      ?: throw BinderAcquisitionException("Unable to get IWifiManager")
}
