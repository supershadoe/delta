package dev.shadoe.delta.data.modules

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.shadoe.delta.data.qualifiers.SoftApBackgroundTasksScope
import javax.inject.Singleton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

@Module
@InstallIn(SingletonComponent::class)
object CoroutineScopesModule {
  @Singleton
  @SoftApBackgroundTasksScope
  @Provides
  fun providesSoftApBackgroundTasksScope(): CoroutineScope =
    CoroutineScope(SupervisorJob() + Dispatchers.Default)
}
