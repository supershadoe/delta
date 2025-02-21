package dev.shadoe.hotspotapi

import android.annotation.SuppressLint
import android.net.ITetheringConnector
import android.net.ITetheringEventCallback
import android.net.TetheringManager
import android.net.TetheringManager.TETHERING_WIFI
import android.net.wifi.ISoftApCallback
import android.net.wifi.IStringListener
import android.net.wifi.IWifiManager
import android.net.wifi.SoftApConfigurationHidden
import android.net.wifi.WifiSsid
import android.os.Build
import dev.rikka.tools.refine.Refine
import dev.shadoe.hotspotapi.TetheringExceptions.BinderAcquisitionException
import dev.shadoe.hotspotapi.Utils.generateRandomPassword
import dev.shadoe.hotspotapi.Utils.hasBit
import dev.shadoe.hotspotapi.callbacks.SoftApCallback
import dev.shadoe.hotspotapi.callbacks.StartTetheringCallback
import dev.shadoe.hotspotapi.callbacks.StopTetheringCallback
import dev.shadoe.hotspotapi.callbacks.TetheringEventCallback
import dev.shadoe.hotspotapi.callbacks.TetheringResultListener
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow
import org.lsposed.hiddenapibypass.HiddenApiBypass
import rikka.shizuku.ShizukuBinderWrapper
import rikka.shizuku.SystemServiceHelper
import kotlin.time.Duration.Companion.seconds

class HotspotApi(
    private val packageName: String,
    private val attributionTag: String?,
) {
    private val tetheringConnector: ITetheringConnector
    private val wifiManager: IWifiManager

    private val _config: MutableStateFlow<SoftApConfiguration>
    private val _getSoftApConfigFlow: Flow<SoftApConfigurationHidden> = flow {
        while (true) {
            emit(Refine.unsafeCast<SoftApConfigurationHidden>(wifiManager.softApConfiguration))
            delay(1.seconds)
        }
    }
    private val _enabledState: MutableStateFlow<Int>
    private val _tetheredClients: MutableStateFlow<List<TetheredClientWrapper>>
    private val _fallbackPassphrase: MutableStateFlow<String>
    private val _supportedSpeedTypes: MutableStateFlow<List<Int>>
    private val _maxClientLimit: MutableStateFlow<Int>

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

        val softApConfiguration =
            Refine.unsafeCast<SoftApConfigurationHidden>(wifiManager.softApConfiguration)
        _config =
            MutableStateFlow(parseSoftApConfiguration(softApConfiguration))
        _enabledState = MutableStateFlow(wifiManager.wifiApEnabledState)
        _tetheredClients = MutableStateFlow(emptyList())
        _fallbackPassphrase = MutableStateFlow(generateRandomPassword())
        _supportedSpeedTypes = MutableStateFlow(emptyList())
        _maxClientLimit =
            MutableStateFlow(softApConfiguration.maxNumberOfClients)

        tetheringEventCallback = TetheringEventCallback(
            updateEnabledState = {
                _enabledState.value = wifiManager.wifiApEnabledState
            },
            setTetheredClients = { _tetheredClients.value = it },
        )

        softApCallback = SoftApCallback(
            setSupportedSpeedTypes = {
                _supportedSpeedTypes.value = it
            },
            setMaxClientLimit = { _maxClientLimit.value },
        )

        // It is enough to call this function only once per session as
        // our config data class remembers previous passphrase.
        wifiManager.queryLastConfiguredTetheredApPassphraseSinceBoot(
            object : IStringListener.Stub() {
                override fun onResult(value: String?) {
                    _fallbackPassphrase.value = value ?: generateRandomPassword()
                }
            }
        )
    }

    val config: StateFlow<SoftApConfiguration> = _config
    val enabledState: StateFlow<Int> = _enabledState
    val tetheredClients: StateFlow<List<TetheredClientWrapper>> =
        _tetheredClients
    val supportedSpeedTypes: StateFlow<List<Int>> = _supportedSpeedTypes

    fun setSoftApConfiguration(c: SoftApConfiguration): Boolean =
        SoftApConfigurationHidden.Builder().apply {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                setWifiSsid(c.ssid?.encodeToByteArray()?.let {
                    WifiSsid.fromBytes(it)
                })
            } else {
                @Suppress("DEPRECATION") setSsid(c.ssid)
            }

            val passphrase =
                if (c.securityType == SoftApSecurityType.SECURITY_TYPE_OPEN) {
                    null
                } else {
                    c.passphrase
                }

            setPassphrase(
                passphrase,
                @SuppressLint("WrongConstant") c.securityType
            )

            setBssid(c.bssid)
            setHiddenSsid(c.isHidden)
            setBlockedClientList(c.blockedDevices)

            val band2To5 =
                SoftApSpeedType.BAND_2GHZ or SoftApSpeedType.BAND_5GHZ
            val band2To6 =
                SoftApSpeedType.BAND_2GHZ or SoftApSpeedType.BAND_5GHZ or SoftApSpeedType.BAND_6GHZ
            when (c.speedType) {
                SoftApSpeedType.BAND_6GHZ -> setBand(band2To6)
                SoftApSpeedType.BAND_5GHZ -> {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                        setBands(
                            intArrayOf(
                                SoftApSpeedType.BAND_2GHZ, band2To5,
                            )
                        )
                    }
                    setBand(band2To5)
                }
                SoftApSpeedType.BAND_2GHZ -> {
                    setBand(SoftApSpeedType.BAND_2GHZ)
                }

                else -> {}
            }
        }.build().let {
            Refine.unsafeCast<android.net.wifi.SoftApConfiguration>(it).let {
                if (!wifiManager.validateSoftApConfiguration(it)) {
                    return false
                }
                _config.value = c
                wifiManager.setSoftApConfiguration(it, ADB_PACKAGE_NAME)
                return true
            }
        }

    fun registerCallback() {
        tetheringConnector.registerTetheringEventCallback(
            tetheringEventCallback, packageName
        )
        wifiManager.registerSoftApCallback(softApCallback)
    }

    suspend fun launchBackgroundTasks() {
        _getSoftApConfigFlow.collect {
            _config.value = parseSoftApConfiguration(it)
        }
    }

    fun unregisterCallback() {
        tetheringConnector.unregisterTetheringEventCallback(
            tetheringEventCallback, packageName
        )
        wifiManager.unregisterSoftApCallback(softApCallback)
    }

    fun startHotspot() {
        if (_enabledState.value != SoftApEnabledState.WIFI_AP_STATE_DISABLED) {
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
        if (_enabledState.value != SoftApEnabledState.WIFI_AP_STATE_ENABLED) {
            return
        }
        tetheringConnector.stopTethering(
            TETHERING_WIFI,
            packageName,
            attributionTag,
            TetheringResultListener(StopTetheringCallback()),
        )
    }

    private fun parseSoftApConfiguration(c: SoftApConfigurationHidden) =
        SoftApConfiguration(
            ssid = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                c.wifiSsid?.bytes?.decodeToString()
            } else {
                @Suppress("DEPRECATION") c.ssid
            },
            passphrase = c.passphrase ?: _fallbackPassphrase.value,
            securityType = @SuppressLint("WrongConstant") c.securityType,
            bssid = c.bssid,
            isHidden = c.isHiddenSsid,
            speedType = c.bands.max().run {
                when {
                    this hasBit SoftApSpeedType.BAND_6GHZ -> {
                        SoftApSpeedType.BAND_6GHZ
                    }

                    this hasBit SoftApSpeedType.BAND_5GHZ -> {
                        SoftApSpeedType.BAND_5GHZ
                    }

                    this hasBit SoftApSpeedType.BAND_2GHZ -> {
                        SoftApSpeedType.BAND_2GHZ
                    }

                    else -> {
                        SoftApSpeedType.BAND_UNKNOWN
                    }
                }
            },
            blockedDevices = c.blockedClientList,
            isAutoShutdownEnabled = c.isAutoShutdownEnabled,
        )
}
