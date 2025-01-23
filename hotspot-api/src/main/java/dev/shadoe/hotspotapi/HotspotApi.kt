package dev.shadoe.hotspotapi

import android.net.ITetheringConnector
import android.net.ITetheringEventCallback
import android.net.TetheringManager
import android.net.TetheringManager.TETHERING_WIFI
import android.net.wifi.IWifiManager
import android.os.Build
import dev.shadoe.hotspotapi.TetheringExceptions.BinderAcquisitionException
import dev.shadoe.hotspotapi.callbacks.StartTetheringCallback
import dev.shadoe.hotspotapi.callbacks.StopTetheringCallback
import dev.shadoe.hotspotapi.callbacks.TetheringEventCallback
import dev.shadoe.hotspotapi.callbacks.TetheringResultListener
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.mapLatest
import org.lsposed.hiddenapibypass.HiddenApiBypass
import rikka.shizuku.ShizukuBinderWrapper
import rikka.shizuku.SystemServiceHelper

class HotspotApi(
    private val packageName: String,
    private val attributionTag: String?,
) {
    private val tetheringConnector: ITetheringConnector
    private val wifiManager: IWifiManager
    private val tetheringEventCallback: ITetheringEventCallback
    private val hotspotState: HotspotState

    init {
        HiddenApiBypass.setHiddenApiExemptions("L")

        tetheringConnector = SystemServiceHelper.getSystemService("tethering")
            ?.let { ShizukuBinderWrapper(it) }
            ?.let { ITetheringConnector.Stub.asInterface(it) }
            ?: throw BinderAcquisitionException(
                "Unable to get ITetheringConnector"
            )

        wifiManager = SystemServiceHelper.getSystemService("wifi")
            ?.let { ShizukuBinderWrapper(it) }
            ?.let { IWifiManager.Stub.asInterface(it) }
            ?: throw BinderAcquisitionException("Unable to get IWifiManager")

        tetheringEventCallback = TetheringEventCallback()
        hotspotState = HotspotState(wifiManager.wifiApEnabledState)
    }

    val ssid = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        wifiManager.softApConfiguration.wifiSsid?.bytes?.decodeToString()
            ?: "null"
    } else {
        @Suppress("DEPRECATION") wifiManager.softApConfiguration.ssid
    }
    val passphrase = wifiManager.softApConfiguration.passphrase
    val securityType = wifiManager.softApConfiguration.securityType

    @OptIn(ExperimentalCoroutinesApi::class)
    val isHotspotRunning =
        hotspotState.enabledState.mapLatest { it == WifiApEnabledStates.WIFI_AP_STATE_ENABLED }

    @OptIn(ExperimentalCoroutinesApi::class)
    val isHotspotDisabled =
        hotspotState.enabledState.mapLatest { it == WifiApEnabledStates.WIFI_AP_STATE_DISABLED }

    fun registerCallback() =
        // TODO: figure out how to get data from this callback
        tetheringConnector.registerTetheringEventCallback(
            tetheringEventCallback, packageName
        )

    fun unregisterCallback() =
        tetheringConnector.unregisterTetheringEventCallback(
            tetheringEventCallback, packageName
        )

    fun startHotspot() {
        if (hotspotState.enabledState.value != WifiApEnabledStates.WIFI_AP_STATE_DISABLED) {
            return
        }
        val request = TetheringManager.TetheringRequest.Builder(TETHERING_WIFI)
            .setSoftApConfiguration(wifiManager.softApConfiguration).build()
        tetheringConnector.startTethering(
            request.parcel,
            packageName,
            attributionTag,
            TetheringResultListener(StartTetheringCallback()),
        )
    }

    fun stopHotspot() {
        if (hotspotState.enabledState.value != WifiApEnabledStates.WIFI_AP_STATE_ENABLED) {
            return
        }
        tetheringConnector.stopTethering(
            TETHERING_WIFI,
            packageName,
            attributionTag,
            TetheringResultListener(StopTetheringCallback()),
        )
    }
}
