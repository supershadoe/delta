package dev.shadoe.delta.data.modules

import android.net.ITetheringConnector
import android.net.wifi.IWifiManager
import android.os.IBinder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.shadoe.delta.data.qualifiers.TetheringSystemService
import dev.shadoe.delta.data.qualifiers.WifiSystemService
import javax.inject.Singleton
import rikka.shizuku.ShizukuBinderWrapper
import rikka.shizuku.SystemServiceHelper

@Module
@InstallIn(SingletonComponent::class)
object SystemServicesModule {
  class BinderAcquisitionException(message: String) : Exception(message)

  private fun getSystemService(name: String): IBinder {
    return SystemServiceHelper.getSystemService(name)?.let {
      ShizukuBinderWrapper(it)
    } ?: throw BinderAcquisitionException("Unable to get service: $name")
  }

  @Singleton
  @TetheringSystemService
  @Provides
  fun provideTetheringManager(): ITetheringConnector =
    ITetheringConnector.Stub.asInterface(getSystemService("tethering"))

  @Singleton
  @WifiSystemService
  @Provides
  fun provideWifiManager(): IWifiManager =
    IWifiManager.Stub.asInterface(getSystemService("wifi"))
}
