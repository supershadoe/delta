package dev.shadoe.hotspotapi

import android.net.ITetheringConnector
import android.net.ITetheringEventCallback
import android.net.TetheringManager
import android.net.TetheringManager.TETHERING_WIFI
import android.net.wifi.ISoftApCallback
import android.net.wifi.IStringListener
import android.net.wifi.IWifiManager
import android.net.wifi.SoftApConfigurationHidden
import dev.rikka.tools.refine.Refine
import dev.shadoe.hotspotapi.Extensions.toBridgeClass
import dev.shadoe.hotspotapi.Extensions.toOriginalClass
import dev.shadoe.hotspotapi.TetheringExceptions.BinderAcquisitionException
import dev.shadoe.hotspotapi.Utils.generateRandomPassword
import dev.shadoe.hotspotapi.callbacks.SoftApCallback
import dev.shadoe.hotspotapi.callbacks.StartTetheringCallback
import dev.shadoe.hotspotapi.callbacks.StopTetheringCallback
import dev.shadoe.hotspotapi.callbacks.TetheringEventCallback
import dev.shadoe.hotspotapi.callbacks.TetheringResultListener
import dev.shadoe.hotspotapi.helper.SoftApEnabledState
import dev.shadoe.hotspotapi.helper.TetheredClientWrapper
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
    private val pollSoftApConfigFlow: Flow<SoftApConfigurationHidden>
    private val _enabledState: MutableStateFlow<Int>
    private val _tetheredClients: MutableStateFlow<List<TetheredClientWrapper>>
    private val fallbackPassphrase: MutableStateFlow<String>
    private val _supportedSpeedTypes: MutableStateFlow<List<Int>>
    private val maxClientLimit: MutableStateFlow<Int>

    private val tetheringEventCallback: ITetheringEventCallback
    private val softApCallback: ISoftApCallback

    companion object {
        private const val ADB_PACKAGE_NAME = "com.android.shell"
    }

    init {
        HiddenApiBypass.setHiddenApiExemptions("L")

        tetheringConnector = SystemServiceHelper
            .getSystemService("tethering")
            ?.let { ShizukuBinderWrapper(it) }
            ?.let { ITetheringConnector.Stub.asInterface(it) }
            ?: throw BinderAcquisitionException(
                "Unable to get ITetheringConnector",
            )

        wifiManager = SystemServiceHelper
            .getSystemService("wifi")
            ?.let { ShizukuBinderWrapper(it) }
            ?.let { IWifiManager.Stub.asInterface(it) }
            ?: throw BinderAcquisitionException("Unable to get IWifiManager")

        val softApConfiguration =
            Refine.unsafeCast<SoftApConfigurationHidden>(
                wifiManager.softApConfiguration,
            )
        _enabledState = MutableStateFlow(wifiManager.wifiApEnabledState)
        _tetheredClients = MutableStateFlow(emptyList())
        fallbackPassphrase = MutableStateFlow(generateRandomPassword())
        _supportedSpeedTypes = MutableStateFlow(emptyList())
        maxClientLimit =
            MutableStateFlow(softApConfiguration.maxNumberOfClients)
        _config =
            MutableStateFlow(
                softApConfiguration.toBridgeClass(
                    fallbackPassphrase = fallbackPassphrase.value,
                ),
            )
        pollSoftApConfigFlow =
            flow {
                while (true) {
                    emit(
                        Refine.unsafeCast<SoftApConfigurationHidden>(
                            wifiManager.softApConfiguration,
                        ),
                    )
                    delay(1.seconds)
                }
            }

        tetheringEventCallback =
            TetheringEventCallback(
                updateEnabledState = {
                    _enabledState.value = wifiManager.wifiApEnabledState
                },
                setTetheredClients = { _tetheredClients.value = it },
            )

        softApCallback =
            SoftApCallback(
                setSupportedSpeedTypes = {
                    _supportedSpeedTypes.value = it
                },
                setMaxClientLimit = { maxClientLimit.value },
            )

        // It is enough to call this function only once per session as
        // our config data class remembers previous passphrase.
        wifiManager.queryLastConfiguredTetheredApPassphraseSinceBoot(
            object :
                IStringListener.Stub() {
                override fun onResult(value: String?) {
                    fallbackPassphrase.value = value ?: generateRandomPassword()
                }
            },
        )
    }

    val config: StateFlow<SoftApConfiguration> = _config
    val enabledState: StateFlow<Int> = _enabledState
    val tetheredClients: StateFlow<List<TetheredClientWrapper>> =
        _tetheredClients
    val supportedSpeedTypes: StateFlow<List<Int>> = _supportedSpeedTypes

    fun setSoftApConfiguration(c: SoftApConfiguration): Boolean =
        runCatching {
            Refine
                .unsafeCast<android.net.wifi.SoftApConfiguration>(
                    c.toOriginalClass(),
                ).let {
                    if (!wifiManager.validateSoftApConfiguration(it)) {
                        return false
                    }
                    _config.value = c
                    wifiManager.setSoftApConfiguration(it, ADB_PACKAGE_NAME)
                    return true
                }
        }.getOrDefault(false)

    fun registerCallback() {
        tetheringConnector.registerTetheringEventCallback(
            tetheringEventCallback,
            packageName,
        )
        wifiManager.registerSoftApCallback(softApCallback)
    }

    suspend fun launchBackgroundTasks() {
        pollSoftApConfigFlow.collect {
            _config.value =
                it.toBridgeClass(fallbackPassphrase = fallbackPassphrase.value)
        }
    }

    fun unregisterCallback() {
        tetheringConnector.unregisterTetheringEventCallback(
            tetheringEventCallback,
            packageName,
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
}
