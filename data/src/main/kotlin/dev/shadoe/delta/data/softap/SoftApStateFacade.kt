package dev.shadoe.delta.data.softap

import dev.shadoe.delta.api.ShizukuStates
import dev.shadoe.delta.data.qualifiers.SoftApBackgroundTasksScope
import dev.shadoe.delta.data.shizuku.ShizukuRepository
import javax.inject.Inject
import javax.inject.Provider
import javax.inject.Singleton
import kotlin.concurrent.atomics.AtomicInt
import kotlin.concurrent.atomics.ExperimentalAtomicApi
import kotlin.concurrent.atomics.decrementAndFetch
import kotlin.concurrent.atomics.incrementAndFetch
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.launch

@OptIn(ExperimentalAtomicApi::class)
@Singleton
class SoftApStateFacade
@Inject
constructor(
  private val shizukuRepository: ShizukuRepository,
  private val listenerProvider: Provider<SoftApStateListener>,
  private val backgroundJobsProvider: Provider<SoftApMonitor>,
  state: SoftApStateStore,
  @SoftApBackgroundTasksScope private val scope: CoroutineScope,
) {
  private var softApStateListener: SoftApStateListener? = null
  private var softApMonitor: SoftApMonitor? = null
  private var shizukuSubscriber: AutoCloseable? = null
  private var shizukuStateCollector: Job? = null

  private var isRunning = false
  private var subscribers = AtomicInt(0)

  val status = state.status
  val config = state.config

  private fun startListening() {
    softApStateListener?.close()
    softApStateListener = listenerProvider.get()
    softApMonitor?.close()
    softApMonitor = backgroundJobsProvider.get()
  }

  private fun stopListening() {
    runCatching {
      softApStateListener?.close()
      softApMonitor?.close()
    }
    softApMonitor = null
  }

  private fun start() {
    shizukuSubscriber?.close()
    shizukuSubscriber = shizukuRepository.callbackSubscriber
    shizukuStateCollector?.cancel()
    shizukuStateCollector =
      scope.launch {
        @OptIn(ExperimentalCoroutinesApi::class)
        shizukuRepository.shizukuState
          .mapLatest { it == ShizukuStates.CONNECTED }
          .distinctUntilChanged()
          .collectLatest { if (it) startListening() else stopListening() }
      }
    isRunning = true
  }

  private fun stop() {
    stopListening()
    shizukuSubscriber?.close()
    shizukuSubscriber = null
    shizukuStateCollector?.cancel()
    shizukuStateCollector = null
    isRunning = false
  }

  fun subscribe() {
    val current = subscribers.incrementAndFetch()
    if (current == 1 && !isRunning) {
      start()
    }
  }

  fun unsubscribe() {
    val current = subscribers.decrementAndFetch()
    if (current < 0) subscribers.store(0)
    if (current == 0 && isRunning) stop()
  }
}
