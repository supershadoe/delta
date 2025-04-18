package dev.shadoe.delta.data.softap

import android.net.IIntResultListener
import android.net.ITetheringConnector
import android.net.TetheringManagerHidden
import android.net.TetheringManagerHidden.TETHERING_WIFI
import android.net.wifi.IWifiManager
import android.net.wifi.SoftApConfigurationHidden
import android.os.Build
import android.util.Log
import dev.rikka.tools.refine.Refine
import dev.shadoe.delta.api.SoftApConfiguration
import dev.shadoe.delta.api.SoftApEnabledState
import dev.shadoe.delta.data.qualifiers.TetheringSystemService
import dev.shadoe.delta.data.qualifiers.WifiSystemService
import dev.shadoe.delta.data.softap.internal.Extensions.toBridgeClass
import dev.shadoe.delta.data.softap.internal.Extensions.toOriginalClass
import dev.shadoe.delta.data.softap.internal.Utils.ADB_PACKAGE_NAME
import javax.inject.Inject
import kotlin.time.Duration.Companion.milliseconds
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

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

  private val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())

  private val dummyIntResultReceiver =
    object : IIntResultListener.Stub() {
      override fun onResult(resultCode: Int) {}
    }

  fun startSoftAp(): Boolean {
    val state = softApStateRepository.status.value.enabledState
    if (state != SoftApEnabledState.WIFI_AP_STATE_DISABLED) {
      return false
    }
    val request =
      TetheringManagerHidden.TetheringRequest.Builder(TETHERING_WIFI).build()
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
          scope.launch {
            val enabled = SoftApEnabledState.WIFI_AP_STATE_ENABLED
            val disabled = SoftApEnabledState.WIFI_AP_STATE_DISABLED
            val status = softApStateRepository.mStatus
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
          }
        }
      }

  fun refreshConfiguration() {
    softApStateRepository.mConfig.update {
      Refine.unsafeCast<SoftApConfigurationHidden>(
          wifiManager.softApConfiguration
        )
        .toBridgeClass(state = softApStateRepository.internalState.value)
    }
  }
}
