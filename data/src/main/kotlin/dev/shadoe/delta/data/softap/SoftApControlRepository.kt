package dev.shadoe.delta.data.softap

import android.net.IIntResultListener
import android.net.ITetheringConnector
import android.net.TetheringManager
import android.net.TetheringManager.TETHERING_WIFI
import android.os.Build
import dev.shadoe.delta.api.SoftApEnabledState
import dev.shadoe.delta.data.qualifiers.TetheringSystemService
import dev.shadoe.delta.data.softap.internal.Utils.ADB_PACKAGE_NAME
import javax.inject.Inject

class SoftApControlRepository
@Inject
constructor(
  @TetheringSystemService private val tetheringConnector: ITetheringConnector,
  private val softApRepository: SoftApRepository,
) {
  private val dummyIntResultReceiver =
    object : IIntResultListener.Stub() {
      override fun onResult(resultCode: Int) {}
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
