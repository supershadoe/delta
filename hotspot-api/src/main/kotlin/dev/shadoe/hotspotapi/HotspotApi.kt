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
import dev.shadoe.hotspotapi.wrappers.SoftApEnabledState
import dev.shadoe.hotspotapi.internal.InternalState
import dev.shadoe.hotspotapi.wrappers.SoftApConfiguration
import dev.shadoe.hotspotapi.wrappers.SoftApStatus
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import org.lsposed.hiddenapibypass.HiddenApiBypass
import rikka.shizuku.ShizukuBinderWrapper
import rikka.shizuku.SystemServiceHelper
import java.io.File
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds

private typealias PrefT = Map.Entry<Preferences.Key<String>, String>

open class HotspotApi(
    private val applicationContext: Context,
) : IHotspotApi {
    private val tetheringConnector: ITetheringConnector
    private val wifiManager: IWifiManager
    private val persistedMacAddressCache: DataStore<Preferences>

    private val internalState: MutableStateFlow<InternalState>
    private val _config: MutableStateFlow<SoftApConfiguration>
    private val _status: MutableStateFlow<SoftApStatus>

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

        internalState = MutableStateFlow(InternalState())
        _config =
            MutableStateFlow(
                Refine
                    .unsafeCast<SoftApConfigurationHidden>(
                        wifiManager.softApConfiguration,
                    ).toBridgeClass(
                        state = internalState.value,
                    ),
            )
        _status =
            MutableStateFlow(
                SoftApStatus(
                    enabledState = wifiManager.wifiApEnabledState,
                    tetheredClients = emptyList(),
                    supportedSpeedTypes = emptyList(),
                    maxSupportedClients = 0,
                ),
            )

        tetheringEventCallback =
            TetheringEventCallback(
                updateEnabledState = {
                    _status.update {
                        it.copy(enabledState = wifiManager.wifiApEnabledState)
                    }
                },
                setTetheredClients = { clients ->
                    _status.update {
                        it.copy(tetheredClients = clients)
                    }
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
                    _status.update { st ->
                        st.copy(supportedSpeedTypes = it)
                    }
                },
                setMaxClientLimit = {
                    _status.update { st ->
                        st.copy(maxSupportedClients = it)
                    }
                },
            )

        // It is enough to call this function only once per session as
        // our config data class remembers previous passphrase.
        wifiManager.queryLastConfiguredTetheredApPassphraseSinceBoot(
            object : IStringListener.Stub() {
                override fun onResult(value: String?) {
                    internalState.update {
                        it.copy(
                            fallbackPassphrase =
                                value ?: generateRandomPassword(),
                        )
                    }
                    _config.update {
                        it.copy(
                            passphrase = internalState.value.fallbackPassphrase,
                        )
                    }
                }
            },
        )

        tetheringConnector.registerTetheringEventCallback(
            tetheringEventCallback,
            applicationContext.packageName,
        )
        wifiManager.registerSoftApCallback(softApCallback)
    }

    override val config =
        object : MutableStateFlow<SoftApConfiguration> by _config {
            override var value: SoftApConfiguration
                get() = _config.value
                set(value) {
                    if (setSoftApConfiguration(value)) {
                        _config.value = value
                    }
                }
        }

    override val status = _status.asStateFlow()

    override fun startHotspot(forceRestart: Boolean) {
        var shouldStart =
            _status.value.enabledState ==
                SoftApEnabledState.WIFI_AP_STATE_DISABLED
        if (forceRestart) {
            shouldStart = _status.value.enabledState ==
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

    override fun stopHotspot() {
        if (_status.value.enabledState !=
            SoftApEnabledState.WIFI_AP_STATE_ENABLED
        ) {
            return
        }
        tetheringConnector.stopTethering(
            TETHERING_WIFI,
            applicationContext.packageName,
            applicationContext.attributionTag,
            TetheringResultListener(StopTetheringCallback()),
        )
    }

    fun cleanUp() {
        tetheringConnector.unregisterTetheringEventCallback(
            tetheringEventCallback,
            applicationContext.packageName,
        )
        wifiManager.unregisterSoftApCallback(softApCallback)
    }

    private fun setSoftApConfiguration(c: SoftApConfiguration): Boolean =
        runCatching {
            Refine
                .unsafeCast<android.net.wifi.SoftApConfiguration>(
                    c.toOriginalClass(),
                ).let {
                    if (!wifiManager.validateSoftApConfiguration(it)) {
                        return false
                    }
                    wifiManager.setSoftApConfiguration(it, ADB_PACKAGE_NAME)
                    return true
                }
        }.getOrDefault(false)

    private val updateConfigOnConfigChange =
        flow {
            var prev =
                Refine.unsafeCast<SoftApConfigurationHidden>(
                    wifiManager.softApConfiguration,
                )
            emit(prev)
            while (true) {
                val curr =
                    Refine.unsafeCast<SoftApConfigurationHidden>(
                        wifiManager.softApConfiguration,
                    )
                if (prev != curr) {
                    emit(curr)
                    prev = curr
                }
                delay(1.seconds)
            }
        }.onEach {
            _config.value = it.toBridgeClass(state = internalState.value)
        }

    private val restartHotspotOnConfigChange =
        config.onEach {
            val enabled = SoftApEnabledState.WIFI_AP_STATE_ENABLED
            val disabled = SoftApEnabledState.WIFI_AP_STATE_DISABLED
            if (status.value.enabledState == enabled) {
                stopHotspot()
                while (status.value.enabledState != disabled) {
                    delay(500.milliseconds)
                }
                startHotspot()
                while (status.value.enabledState != enabled) {
                    delay(500.milliseconds)
                }
            }
        }

    private val updateMacAddressCacheInMem =
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
                internalState.update { st ->
                    st.copy(macAddressCache = it)
                }
            }

    fun startBackgroundJobs(scope: CoroutineScope) {
        updateConfigOnConfigChange.launchIn(scope)
        restartHotspotOnConfigChange.launchIn(scope)
        updateMacAddressCacheInMem.launchIn(scope)
    }
}
