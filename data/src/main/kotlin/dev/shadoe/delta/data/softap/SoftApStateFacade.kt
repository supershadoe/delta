package dev.shadoe.delta.data.softap

import javax.inject.Inject
import javax.inject.Provider
import javax.inject.Singleton
import kotlin.concurrent.atomics.AtomicInt
import kotlin.concurrent.atomics.ExperimentalAtomicApi
import kotlin.concurrent.atomics.decrementAndFetch
import kotlin.concurrent.atomics.incrementAndFetch

@OptIn(ExperimentalAtomicApi::class)
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
  private var subscribers = AtomicInt(0)

  val status = state.status
  val config = state.config

  fun start() {
    val current = subscribers.incrementAndFetch()
    if (current == 1) {
      softApStateListener = softApStateListener ?: listenerProvider.get()
      softApBackgroundJobs =
        softApBackgroundJobs ?: backgroundJobsProvider.get()
    }
  }

  fun stop() {
    val current = subscribers.decrementAndFetch()
    if (current < 0) {
      subscribers.store(0)
    }
    if (current == 0) {
      runCatching {
        softApStateListener?.close()
        softApStateListener = null
        softApBackgroundJobs?.close()
        softApBackgroundJobs = null
      }
    }
  }
}
