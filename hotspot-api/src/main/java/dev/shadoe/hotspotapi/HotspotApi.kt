package dev.shadoe.hotspotapi

import android.annotation.SuppressLint
import android.net.ITetheringConnector
import android.net.ITetheringEventCallback
import android.net.MacAddress
import android.net.TetheredClient
import android.net.TetheringManager
import android.net.TetheringManager.TETHERING_WIFI
import android.net.wifi.ISoftApCallback
import android.net.wifi.IWifiManager
import android.net.wifi.SoftApConfiguration
import android.net.wifi.WifiSsid
import android.os.Build
import dev.shadoe.hotspotapi.TetheringExceptions.BinderAcquisitionException
import dev.shadoe.hotspotapi.callbacks.SoftApCallback
import dev.shadoe.hotspotapi.callbacks.StartTetheringCallback
import dev.shadoe.hotspotapi.callbacks.StopTetheringCallback
import dev.shadoe.hotspotapi.callbacks.TetheringEventCallback
import dev.shadoe.hotspotapi.callbacks.TetheringResultListener
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.mapLatest
import org.lsposed.hiddenapibypass.HiddenApiBypass
import rikka.shizuku.ShizukuBinderWrapper
import rikka.shizuku.SystemServiceHelper
import kotlin.time.Duration.Companion.seconds
import android.net.wifi.SoftApConfiguration.Builder as SoftApConfBuilder

class HotspotApi(
    private val packageName: String,
    private val attributionTag: String?,
) {
    private val tetheringConnector: ITetheringConnector
    private val wifiManager: IWifiManager

    private val _softApConfiguration: MutableStateFlow<SoftApConfiguration>
    private val _getSoftApConfigFlow: Flow<SoftApConfiguration> = flow {
        while (true) {
            emit(wifiManager.softApConfiguration)
            delay(1.seconds)
        }
    }
    private val _enabledState: MutableStateFlow<Int>
    private val _tetheredClients: MutableStateFlow<List<TetheredClient>>

    private val tetheringEventCallback: ITetheringEventCallback
    private val softApCallback: ISoftApCallback

    companion object {
        private const val ADB_PACKAGE_NAME = "com.android.shell"
    }

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

        _softApConfiguration = MutableStateFlow(wifiManager.softApConfiguration)
        _enabledState = MutableStateFlow(wifiManager.wifiApEnabledState)
        _tetheredClients = MutableStateFlow(emptyList())

        tetheringEventCallback = TetheringEventCallback(
            updateEnabledState = {
                _enabledState.value = wifiManager.wifiApEnabledState
            },
            setTetheredClients = { _tetheredClients.value = it },
        )

        softApCallback = SoftApCallback()
    }

    val enabledState: StateFlow<Int> = _enabledState
    val tetheredClients: StateFlow<List<TetheredClient>> = _tetheredClients

    @OptIn(ExperimentalCoroutinesApi::class)
    val ssid = _softApConfiguration.mapLatest {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            it.wifiSsid?.bytes?.decodeToString()
        } else {
            @Suppress("DEPRECATION") it.ssid
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    val passphrase = _softApConfiguration.mapLatest { it.passphrase }

    @OptIn(ExperimentalCoroutinesApi::class)
    val securityType = _softApConfiguration.mapLatest { it.securityType }

    @OptIn(ExperimentalCoroutinesApi::class)
    val bssid = _softApConfiguration.mapLatest { it.bssid }

    @OptIn(ExperimentalCoroutinesApi::class)
    val isHidden = _softApConfiguration.mapLatest { it.isHiddenSsid }

    private fun updateSoftApConfiguration(conf: SoftApConfiguration): Boolean {
        if (!wifiManager.validateSoftApConfiguration(conf)) return false
        _softApConfiguration.value = conf
        wifiManager.setSoftApConfiguration(conf, ADB_PACKAGE_NAME)
        return true
    }

    fun setSsid(newSsid: String?): Boolean =
        SoftApConfBuilder(_softApConfiguration.value).apply {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                setWifiSsid(newSsid?.encodeToByteArray()?.let {
                    WifiSsid.fromBytes(it)
                })
            } else {
                @Suppress("DEPRECATION") setSsid(newSsid)
            }
        }.build().let { updateSoftApConfiguration(it) }

    fun setPassphrase(newPassphrase: String?): Boolean =
        _softApConfiguration.value.let { other ->
            @SuppressLint("WrongConstant") SoftApConfBuilder(other).setPassphrase(
                newPassphrase, other.securityType
            ).build().let { updateSoftApConfiguration(it) }
        }

    fun setSecurityType(@SoftApConfiguration.SecurityType newSecurityType: Int): Boolean =
        _softApConfiguration.value.let { other ->
            SoftApConfBuilder(other).setPassphrase(
                other.passphrase, newSecurityType
            ).build().let { updateSoftApConfiguration(it) }
        }

    fun setBssid(newBssid: MacAddress?): Boolean =
        SoftApConfBuilder(_softApConfiguration.value).setBssid(newBssid).build()
            .let { updateSoftApConfiguration(it) }


    fun setIsHidden(newIsHidden: Boolean): Boolean =
        SoftApConfBuilder(_softApConfiguration.value).setHiddenSsid(newIsHidden)
            .build().let { updateSoftApConfiguration(it) }

    fun registerCallback() {
        tetheringConnector.registerTetheringEventCallback(
            tetheringEventCallback, packageName
        )
        wifiManager.registerSoftApCallback(softApCallback)
    }

    suspend fun launchBackgroundTasks() {
        _getSoftApConfigFlow.collect { _softApConfiguration.value = it }
    }

    fun unregisterCallback() {
        tetheringConnector.unregisterTetheringEventCallback(
            tetheringEventCallback, packageName
        )
        wifiManager.unregisterSoftApCallback(softApCallback)
    }

    fun startHotspot() {
        if (_enabledState.value != WifiApEnabledStates.WIFI_AP_STATE_DISABLED) {
            return
        }
        val request =
            TetheringManager.TetheringRequest.Builder(TETHERING_WIFI).build()
        tetheringConnector.startTethering(
            request.parcel,
            packageName,
            attributionTag,
            TetheringResultListener(StartTetheringCallback()),
        )
    }

    fun stopHotspot() {
        if (_enabledState.value != WifiApEnabledStates.WIFI_AP_STATE_ENABLED) {
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
