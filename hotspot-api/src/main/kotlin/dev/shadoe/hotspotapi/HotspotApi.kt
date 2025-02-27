package dev.shadoe.hotspotapi

import android.content.Context
import android.net.ITetheringConnector
import android.net.ITetheringEventCallback
import android.net.MacAddress
import android.net.TetheringManager
import android.net.TetheringManager.TETHERING_WIFI
import android.net.wifi.ISoftApCallback
import android.net.wifi.IStringListener
import android.net.wifi.IWifiManager
import android.net.wifi.SoftApConfigurationHidden
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import org.lsposed.hiddenapibypass.HiddenApiBypass
import rikka.shizuku.ShizukuBinderWrapper
import rikka.shizuku.SystemServiceHelper
import java.io.File
import kotlin.time.Duration.Companion.seconds

private typealias PrefT = Map.Entry<Preferences.Key<String>, String>

open class HotspotApi(
    private val applicationContext: Context,
    private val scope: CoroutineScope,
) {
    private val tetheringConnector: ITetheringConnector
    private val wifiManager: IWifiManager
    private val persistedMacAddressCache: DataStore<Preferences>

    private val _config: MutableStateFlow<SoftApConfiguration>
    private val _enabledState: MutableStateFlow<Int>
    private val _tetheredClients: MutableStateFlow<List<TetheredClientWrapper>>
    private val fallbackPassphrase: MutableStateFlow<String>
    private val _supportedSpeedTypes: MutableStateFlow<List<Int>>
    private val maxClientLimit: MutableStateFlow<Int>
    private val macAddressCache: MutableStateFlow<Map<MacAddress, String>>

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
        persistedMacAddressCache =
            PreferenceDataStoreFactory.create {
                File(
                    applicationContext.filesDir,
                    "datastore/mac_address_cache.preferences_pb",
                )
            }

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
        macAddressCache = MutableStateFlow(emptyMap())
        _config =
            MutableStateFlow(
                softApConfiguration.toBridgeClass(
                    fallbackPassphrase = fallbackPassphrase.value,
                    macAddressCache = macAddressCache.value,
                ),
            )

        tetheringEventCallback =
            TetheringEventCallback(
                updateEnabledState = {
                    _enabledState.value = wifiManager.wifiApEnabledState
                },
                setTetheredClients = { clients ->
                    _tetheredClients.value = clients
                    val a =
                        persistedMacAddressCache.edit { prefs ->
                            clients
                                .filter { it.hostnames.firstOrNull() != null }
                                .map {
                                    stringPreferencesKey(
                                        name = it.macAddress.toString(),
                                    ) to it.hostnames.first()!!
                                }.let {
                                    prefs.putAll(*it.toTypedArray())
                                }
                        }
                },
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

        tetheringConnector.registerTetheringEventCallback(
            tetheringEventCallback,
            applicationContext.packageName,
        )
        wifiManager.registerSoftApCallback(softApCallback)
        startBackgroundJobs()
    }

    val config: StateFlow<SoftApConfiguration> = _config
    val enabledState: StateFlow<Int> = _enabledState
    val tetheredClients: StateFlow<List<TetheredClientWrapper>> =
        _tetheredClients
    val supportedSpeedTypes: StateFlow<List<Int>> = _supportedSpeedTypes

    fun setSoftApConfiguration(c: SoftApConfiguration): Boolean = runCatching {
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

    fun cleanUp() {
        tetheringConnector.unregisterTetheringEventCallback(
            tetheringEventCallback,
            applicationContext.packageName,
        )
        wifiManager.unregisterSoftApCallback(softApCallback)
        scope.cancel("Cleanup requested")
    }

    fun startHotspot(forceRestart: Boolean = false) {
        var shouldStart =
            _enabledState.value ==
                SoftApEnabledState.WIFI_AP_STATE_DISABLED
        if (forceRestart) {
            shouldStart = _enabledState.value ==
                SoftApEnabledState.WIFI_AP_STATE_FAILED
        }
        if (!shouldStart) return
        val request =
            TetheringManager.TetheringRequest.Builder(TETHERING_WIFI).build()
        tetheringConnector.startTethering(
            request.parcel,
            applicationContext.packageName,
            applicationContext.attributionTag,
            TetheringResultListener(StartTetheringCallback()),
        )
    }

    fun stopHotspot() {
        if (_enabledState.value != SoftApEnabledState.WIFI_AP_STATE_ENABLED) {
            return
        }
        tetheringConnector.stopTethering(
            TETHERING_WIFI,
            applicationContext.packageName,
            applicationContext.attributionTag,
            TetheringResultListener(StopTetheringCallback()),
        )
    }

    private fun startBackgroundJobs() {
        flow {
            while (true) {
                emit(
                    Refine.unsafeCast<SoftApConfigurationHidden>(
                        wifiManager.softApConfiguration,
                    ),
                )
                delay(1.seconds)
            }
        }.onEach {
            _config.value =
                it.toBridgeClass(
                    fallbackPassphrase = fallbackPassphrase.value,
                    macAddressCache = macAddressCache.value,
                )
        }.launchIn(scope)

        persistedMacAddressCache.data
            .map {
                it
                    .asMap()
                    .asIterable()
                    .filterIsInstance<PrefT>()
                    .associate {
                        MacAddress.fromString(it.key.name) to it.value
                    }
            }.onEach {
                macAddressCache.value = it
            }.launchIn(scope)
    }
}
