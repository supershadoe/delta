package dev.shadoe.hotspotapi

import android.net.ITetheringConnector
import android.net.ITetheringEventCallback
import android.net.MacAddress
import android.net.TetheringManager
import android.net.TetheringManager.TETHERING_WIFI
import android.net.wifi.IOnWifiDriverCountryCodeChangedListener
import android.net.wifi.ISoftApCallback
import android.net.wifi.IStringListener
import android.net.wifi.IWifiManager
import android.net.wifi.SoftApConfiguration
import android.net.wifi.SoftApConfigurationHidden
import android.net.wifi.WifiSsid
import android.os.Build
import dev.rikka.tools.refine.Refine
import dev.shadoe.hotspotapi.SoftApSpeedType.hasBand
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
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.mapLatest
import org.lsposed.hiddenapibypass.HiddenApiBypass
import rikka.shizuku.ShizukuBinderWrapper
import rikka.shizuku.SystemServiceHelper
import java.util.UUID
import kotlin.time.Duration.Companion.seconds

class HotspotApi(
    private val packageName: String,
    private val attributionTag: String?,
) {
    private val tetheringConnector: ITetheringConnector
    private val wifiManager: IWifiManager

    private val _softApConfiguration: MutableStateFlow<SoftApConfigurationHidden>
    private val _getSoftApConfigFlow: Flow<SoftApConfigurationHidden> = flow {
        while (true) {
            emit(Refine.unsafeCast<SoftApConfigurationHidden>(wifiManager.softApConfiguration))
            delay(1.seconds)
        }
    }
    private val _enabledState: MutableStateFlow<Int>
    private val _tetheredClients: MutableStateFlow<List<TetheredClientWrapper>>
    private val _lastUsedPassphraseSinceBoot: MutableStateFlow<String?>
    private val _supportedSpeedTypes: MutableStateFlow<List<Int>>

    private val countryCodeChangedListener: IOnWifiDriverCountryCodeChangedListener
    private val tetheringEventCallback: ITetheringEventCallback
    private val softApCallback: ISoftApCallback
    private val lastPassphraseListener: IStringListener

    companion object {
        private const val ADB_PACKAGE_NAME = "com.android.shell"

        /*
         * Copyright (C) 2023 The Android Open Source Project
         *
         * Licensed under the Apache License, Version 2.0 (the "License");
         * you may not use this file except in compliance with the License.
         * You may obtain a copy of the License at
         *
         *      http://www.apache.org/licenses/LICENSE-2.0
         *
         * Unless required by applicable law or agreed to in writing, software
         * distributed under the License is distributed on an "AS IS" BASIS,
         * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
         * See the License for the specific language governing permissions and
         * limitations under the License.
         *
         * From https://cs.android.com/android/platform/superproject/main/+/29fbb69343c063b65d71180d04f5d2acaf4f050c:packages/apps/Settings/src/com/android/settings/wifi/repository/WifiHotspotRepository.java;l=168
         */
        private fun generateRandomPassword(): String {
            val randomUUID = UUID.randomUUID().toString()
            //first 12 chars from xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx
            return randomUUID.substring(0, 8) + randomUUID.substring(9, 13)
        }
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

        _softApConfiguration = MutableStateFlow(
            Refine.unsafeCast<SoftApConfigurationHidden>(wifiManager.softApConfiguration)
        )
        _enabledState = MutableStateFlow(wifiManager.wifiApEnabledState)
        _tetheredClients = MutableStateFlow(emptyList())
        _lastUsedPassphraseSinceBoot = MutableStateFlow(null)
        _supportedSpeedTypes = MutableStateFlow(emptyList())

        countryCodeChangedListener =
            object : IOnWifiDriverCountryCodeChangedListener.Stub() {
                override fun onDriverCountryCodeChanged(countryCode: String?) {
                    _supportedSpeedTypes.value = querySupportedSpeedTypes()
                }
            }

        tetheringEventCallback = TetheringEventCallback(
            updateEnabledState = {
                _enabledState.value = wifiManager.wifiApEnabledState
            },
            setTetheredClients = { _tetheredClients.value = it },
        )

        softApCallback = SoftApCallback()
        lastPassphraseListener = object : IStringListener.Stub() {
            override fun onResult(value: String) {
                _lastUsedPassphraseSinceBoot.value = value
            }
        }
    }

    val enabledState: StateFlow<Int> = _enabledState
    val tetheredClients: StateFlow<List<TetheredClientWrapper>> =
        _tetheredClients
    val supportedSpeedTypes: StateFlow<List<Int>> = _supportedSpeedTypes

    @OptIn(ExperimentalCoroutinesApi::class)
    val ssid = _softApConfiguration.mapLatest {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            it.wifiSsid?.bytes?.decodeToString()
        } else {
            @Suppress("DEPRECATION") it.ssid
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    val passphrase = combine(
        _softApConfiguration.mapLatest { it.passphrase },
        _lastUsedPassphraseSinceBoot
    ) { val1, val2 ->
        val1 ?: val2 ?: generateRandomPassword()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    val securityType = _softApConfiguration.mapLatest { it.securityType }

    @OptIn(ExperimentalCoroutinesApi::class)
    val bssid = _softApConfiguration.mapLatest { it.bssid }

    @OptIn(ExperimentalCoroutinesApi::class)
    val isHidden = _softApConfiguration.mapLatest { it.isHiddenSsid }

    @OptIn(ExperimentalCoroutinesApi::class)
    val speedType = _softApConfiguration.mapLatest {
        it.bands.max().run {
            when {
                this hasBand SoftApSpeedType.BAND_6GHZ -> {
                    SoftApSpeedType.BAND_6GHZ
                }
                this hasBand SoftApSpeedType.BAND_5GHZ  -> {
                    SoftApSpeedType.BAND_5GHZ
                }
                this hasBand SoftApSpeedType.BAND_2GHZ  -> {
                    SoftApSpeedType.BAND_2GHZ
                }
                else -> {
                    SoftApSpeedType.BAND_UNKNOWN
                }
            }
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    val blockedDevices = _softApConfiguration.mapLatest { it.blockedClientList }

    @OptIn(ExperimentalCoroutinesApi::class)
    val isAutoShutdownEnabled =
        _softApConfiguration.mapLatest { it.isAutoShutdownEnabled }

    private fun updateSoftApConfigurationHidden(conf: SoftApConfigurationHidden): Boolean {
        Refine.unsafeCast<SoftApConfiguration>(conf).let {
            if (!wifiManager.validateSoftApConfiguration(it)) {
                return false
            }
            _softApConfiguration.value = conf
            wifiManager.setSoftApConfiguration(it, ADB_PACKAGE_NAME)
            return true
        }
    }

    fun setSsid(newSsid: String?): Boolean =
        SoftApConfigurationHidden.Builder(_softApConfiguration.value).apply {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                setWifiSsid(newSsid?.encodeToByteArray()?.let {
                    WifiSsid.fromBytes(it)
                })
            } else {
                @Suppress("DEPRECATION") setSsid(newSsid)
            }
        }.build().let { updateSoftApConfigurationHidden(it) }

    fun setPassphrase(newPassphrase: String?, newSecurityType: Int): Boolean =
        SoftApConfigurationHidden.Builder(_softApConfiguration.value)
            .setPassphrase(
                newPassphrase,
                newSecurityType,
            ).build().let { updateSoftApConfigurationHidden(it) }

    fun setBssid(newBssid: MacAddress?): Boolean =
        SoftApConfigurationHidden.Builder(_softApConfiguration.value)
            .setBssid(newBssid).build()
            .let { updateSoftApConfigurationHidden(it) }

    fun setIsHidden(newIsHidden: Boolean): Boolean =
        SoftApConfigurationHidden.Builder(_softApConfiguration.value)
            .setHiddenSsid(newIsHidden).build()
            .let { updateSoftApConfigurationHidden(it) }

    fun setBlockedDevices(newList: List<MacAddress>): Boolean =
        SoftApConfigurationHidden.Builder(_softApConfiguration.value)
            .setBlockedClientList(newList).build()
            .let { updateSoftApConfigurationHidden(it) }

    fun setSpeedType(newSpeedType: Int): Boolean =
        SoftApConfigurationHidden.Builder(_softApConfiguration.value).apply {
            val band2To5 =
                SoftApSpeedType.BAND_2GHZ or SoftApSpeedType.BAND_5GHZ
            val band2To6 =
                SoftApSpeedType.BAND_2GHZ or SoftApSpeedType.BAND_5GHZ or SoftApSpeedType.BAND_6GHZ
            if (newSpeedType == SoftApSpeedType.BAND_6GHZ) {
                setBand(band2To6)
            } else if (newSpeedType == SoftApSpeedType.BAND_5GHZ) {
                setBand(band2To5)
            } else if (isDualBandSupported() && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                setBands(
                    intArrayOf(
                        SoftApSpeedType.BAND_2GHZ, band2To5,
                    )
                )
            } else {
                setBand(SoftApSpeedType.BAND_2GHZ)
            }
        }.build().let { updateSoftApConfigurationHidden(it) }

    fun setAutoShutdownState(newState: Boolean): Boolean =
        SoftApConfigurationHidden.Builder(_softApConfiguration.value).apply {
            setAutoShutdownEnabled(newState)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                setBridgedModeOpportunisticShutdownEnabled(newState)
            }
        }.build().let { updateSoftApConfigurationHidden(it) }

    fun registerCallback() {
        tetheringConnector.registerTetheringEventCallback(
            tetheringEventCallback, packageName
        )
        wifiManager.registerSoftApCallback(softApCallback)
        wifiManager.registerDriverCountryCodeChangedListener(
            countryCodeChangedListener, ADB_PACKAGE_NAME, attributionTag
        )
    }

    suspend fun launchBackgroundTasks() {
        _getSoftApConfigFlow.collect { _softApConfiguration.value = it }
    }

    fun unregisterCallback() {
        tetheringConnector.unregisterTetheringEventCallback(
            tetheringEventCallback, packageName
        )
        wifiManager.unregisterSoftApCallback(softApCallback)
        wifiManager.unregisterDriverCountryCodeChangedListener(
            countryCodeChangedListener
        )
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

    fun queryLastUsedPassphraseSinceBoot() =
        wifiManager.queryLastConfiguredTetheredApPassphraseSinceBoot(
            lastPassphraseListener
        )

    private fun querySupportedSpeedTypes(): List<Int> {
        val supportedSpeedTypes = mutableListOf<Int>()
        if (wifiManager.is24GHzBandSupported) {
            supportedSpeedTypes.add(SoftApSpeedType.BAND_2GHZ)
        }
        if (wifiManager.is5GHzBandSupported) {
            supportedSpeedTypes.add(SoftApSpeedType.BAND_5GHZ)
        }
        if (wifiManager.is6GHzBandSupported) {
            supportedSpeedTypes.add(SoftApSpeedType.BAND_6GHZ)
        }
        return supportedSpeedTypes.toList()
    }

    private fun isDualBandSupported() =
        wifiManager.supportedFeatures and SoftApFeature.WIFI_FEATURE_STA_BRIDGED_AP == SoftApFeature.WIFI_FEATURE_STA_BRIDGED_AP
}
