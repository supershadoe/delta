package dev.shadoe.delta.data.services

import android.net.ITetheringConnector
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.shadoe.delta.data.exceptions.BinderAcquisitionException
import rikka.shizuku.ShizukuBinderWrapper
import rikka.shizuku.SystemServiceHelper

@Module
@InstallIn(SingletonComponent::class)
object TetheringModule {
    @TetheringSystemService
    @Provides
    fun provideTetheringManager(): ITetheringConnector = SystemServiceHelper
        .getSystemService("tethering")
        ?.let { ShizukuBinderWrapper(it) }
        ?.let { ITetheringConnector.Stub.asInterface(it) }
        ?: throw BinderAcquisitionException(
            "Unable to get ITetheringConnector",
        )
}
