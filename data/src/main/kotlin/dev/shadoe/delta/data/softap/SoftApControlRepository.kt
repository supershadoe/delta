package dev.shadoe.delta.data.softap

import android.net.IIntResultListener
import android.net.ITetheringConnector
import android.net.TetheringManager
import android.net.TetheringManager.TETHERING_WIFI
import android.net.wifi.IWifiManager
import android.os.Build
import android.util.Log
import dev.rikka.tools.refine.Refine
import dev.shadoe.delta.api.SoftApConfiguration
import dev.shadoe.delta.api.SoftApEnabledState
import dev.shadoe.delta.data.qualifiers.TetheringSystemService
import dev.shadoe.delta.data.qualifiers.WifiSystemService
import dev.shadoe.delta.data.softap.internal.Extensions.toOriginalClass
import dev.shadoe.delta.data.softap.internal.Utils.ADB_PACKAGE_NAME
import javax.inject.Inject
import kotlinx.coroutines.flow.update

class SoftApControlRepository
@Inject
constructor(
  @TetheringSystemService private val tetheringConnector: ITetheringConnector,
  @WifiSystemService private val wifiManager: IWifiManager,
  private val softApStateRepository: SoftApStateRepository,
) {
  companion object {
    private const val TAG = "SoftApControlRepository"
  }

  private val dummyIntResultReceiver =
    object : IIntResultListener.Stub() {
      override fun onResult(resultCode: Int) {}
    }

  fun startSoftAp(forceRestart: Boolean = false): Boolean {
    val enabledState = softApStateRepository.status.value.enabledState
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
    val state = softApStateRepository.status.value.enabledState
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

  private fun setSoftApConfiguration(c: SoftApConfiguration) =
    Refine.unsafeCast<android.net.wifi.SoftApConfiguration>(c.toOriginalClass())
      .let {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
          if (!wifiManager.validateSoftApConfiguration(it)) {
            return@let false
          }
        }
        wifiManager.setSoftApConfiguration(it, ADB_PACKAGE_NAME)
        return@let true
      }

  fun updateSoftApConfiguration(c: SoftApConfiguration): Boolean =
    runCatching { setSoftApConfiguration(c) }
      .onFailure { Log.e(TAG, it.stackTraceToString()) }
      .getOrDefault(false)
      .also {
        if (it) {
          softApStateRepository.mConfig.update { c }
          softApStateRepository.internalState.update {
            it.copy(shouldRestart = true)
          }
        }
      }
}
