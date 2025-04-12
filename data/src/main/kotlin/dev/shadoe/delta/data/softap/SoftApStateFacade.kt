package dev.shadoe.delta.data.softap

import javax.inject.Inject
import javax.inject.Provider
import javax.inject.Singleton

@Singleton
class SoftApStateFacade
@Inject
constructor(
  private val listenerProvider: Provider<SoftApStateListener>,
  private val backgroundJobsProvider: Provider<SoftApBackgroundJobs>,
  state: SoftApStateRepository,
) {
  private var softApStateListener: SoftApStateListener? = null
  private var softApBackgroundJobs: SoftApBackgroundJobs? = null

  val status = state.status
  val config = state.config

  fun start() {
    softApStateListener = softApStateListener ?: listenerProvider.get()
    softApBackgroundJobs = softApBackgroundJobs ?: backgroundJobsProvider.get()
  }

  fun stop() {
    runCatching {
      softApStateListener?.close()
      softApStateListener = null
      softApBackgroundJobs?.close()
      softApBackgroundJobs = null
    }
  }
}
