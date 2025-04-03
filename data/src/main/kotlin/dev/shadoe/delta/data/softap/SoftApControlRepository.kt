package dev.shadoe.delta.data.softap

import android.net.IIntResultListener
import android.net.ITetheringConnector
import android.net.TetheringManager
import android.net.TetheringManager.TETHERING_WIFI
import android.os.Build
import dev.shadoe.delta.api.SoftApEnabledState
import dev.shadoe.delta.data.qualifiers.TetheringSystemService
import javax.inject.Inject
import kotlin.time.Duration.Companion.milliseconds
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class SoftApControlRepository
@Inject
constructor(
  @TetheringSystemService private val tetheringConnector: ITetheringConnector,
  private val softApRepository: SoftApRepository,
) : AutoCloseable {
  companion object {
    private const val ADB_PACKAGE_NAME = "com.android.shell"
  }

  private val dummyIntResultReceiver =
    object : IIntResultListener.Stub() {
      override fun onResult(resultCode: Int) {}
    }

  private val scope = CoroutineScope(Dispatchers.Unconfined + SupervisorJob())

  private val shouldRestart = MutableStateFlow(false)

  internal val restartOnConfigChange =
    shouldRestart.onEach {
      if (!it) return@onEach

      val enabled = SoftApEnabledState.WIFI_AP_STATE_ENABLED
      val disabled = SoftApEnabledState.WIFI_AP_STATE_DISABLED
      val status = softApRepository.status
      if (status.value.enabledState == enabled) {
        stopSoftAp()
        while (status.value.enabledState != disabled) {
          delay(500.milliseconds)
        }
        startSoftAp()
        while (status.value.enabledState != enabled) {
          delay(500.milliseconds)
        }
      }

      shouldRestart.value = false
    }

  init {
    restartOnConfigChange.launchIn(scope)
  }

  override fun close() {
    scope.cancel()
  }

  fun startSoftAp(forceRestart: Boolean = false): Boolean {
    val enabledState = softApRepository.status.value.enabledState
    var shouldStart = enabledState == SoftApEnabledState.WIFI_AP_STATE_DISABLED
    if (forceRestart) {
      shouldStart = enabledState == SoftApEnabledState.WIFI_AP_STATE_FAILED
    }
    if (!shouldStart) return false
    val request =
      TetheringManager.TetheringRequest.Builder(TETHERING_WIFI).build()
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
      tetheringConnector.startTethering(
        request.parcel,
        ADB_PACKAGE_NAME,
        null,
        dummyIntResultReceiver,
      )
    } else {
      @Suppress("DEPRECATION")
      tetheringConnector.startTethering(
        request.parcel,
        ADB_PACKAGE_NAME,
        dummyIntResultReceiver,
      )
    }
    return true
  }

  fun stopSoftAp(): Boolean {
    val state = softApRepository.status.value.enabledState
    if (state != SoftApEnabledState.WIFI_AP_STATE_ENABLED) {
      return false
    }
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
      tetheringConnector.stopTethering(
        TETHERING_WIFI,
        ADB_PACKAGE_NAME,
        null,
        dummyIntResultReceiver,
      )
    } else {
      @Suppress("DEPRECATION")
      tetheringConnector.stopTethering(
        TETHERING_WIFI,
        ADB_PACKAGE_NAME,
        dummyIntResultReceiver,
      )
    }
    return true
  }
}
