package dev.shadoe.hotspotapi.internal

import android.annotation.SuppressLint
import android.net.wifi.SoftApConfigurationHidden
import android.net.wifi.WifiSsid
import android.os.Build
import dev.shadoe.hotspotapi.wrappers.ACLDevice
import dev.shadoe.hotspotapi.wrappers.SoftApConfiguration
import dev.shadoe.hotspotapi.wrappers.SoftApSecurityType
import dev.shadoe.hotspotapi.wrappers.SoftApSpeedType

internal object Extensions {
    infix fun Int.hasBit(other: Int): Boolean = (this and other) == other

    fun SoftApConfigurationHidden.toBridgeClass(state: InternalState) =
        SoftApConfiguration(
            ssid =
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    wifiSsid?.bytes?.decodeToString()
                } else {
                    @Suppress("DEPRECATION")
                    ssid
                },
            passphrase = passphrase ?: state.fallbackPassphrase,
            securityType = @SuppressLint("WrongConstant") securityType,
            bssid = bssid,
            isHidden = isHiddenSsid,
            speedType =
                bands.max().run {
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
            blockedDevices =
                blockedClientList.map {
                    ACLDevice(
                        hostname = state.macAddressCache[it],
                        macAddress = it,
                    )
                },
            allowedClients =
                allowedClientList.map {
                    ACLDevice(
                        hostname = state.macAddressCache[it],
                        macAddress = it,
                    )
                },
            isAutoShutdownEnabled = isAutoShutdownEnabled,
            maxClientLimit = maxNumberOfClients,
        )

    fun SoftApConfiguration.toOriginalClass() = SoftApConfigurationHidden
        .Builder()
        .apply {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                setWifiSsid(
                    ssid?.encodeToByteArray()?.let {
                        WifiSsid.fromBytes(it)
                    },
                )
            } else {
                @Suppress("DEPRECATION")
                setSsid(ssid)
            }

            val passphrase =
                if (securityType == SoftApSecurityType.SECURITY_TYPE_OPEN) {
                    null
                } else {
                    passphrase
                }

            setPassphrase(
                passphrase,
                @SuppressLint("WrongConstant") securityType,
            )

            setBssid(bssid)
            setHiddenSsid(isHidden)

            val band2To5 =
                SoftApSpeedType.BAND_2GHZ or SoftApSpeedType.BAND_5GHZ
            val band2To6 =
                SoftApSpeedType.BAND_2GHZ or SoftApSpeedType.BAND_5GHZ or
                    SoftApSpeedType.BAND_6GHZ
            when (speedType) {
                SoftApSpeedType.BAND_6GHZ -> setBand(band2To6)
                SoftApSpeedType.BAND_5GHZ -> {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                        setBands(
                            intArrayOf(
                                SoftApSpeedType.BAND_2GHZ,
                                band2To5,
                            ),
                        )
                    }
                    setBand(band2To5)
                }

                SoftApSpeedType.BAND_2GHZ -> {
                    setBand(SoftApSpeedType.BAND_2GHZ)
                }

                else -> {}
            }

            setBlockedClientList(
                blockedDevices.map { it.macAddress },
            )
            setAllowedClientList(
                allowedClients.map { it.macAddress },
            )
            setClientControlByUserEnabled(false)
            setAutoShutdownEnabled(isAutoShutdownEnabled)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                setBridgedModeOpportunisticShutdownEnabled(
                    isAutoShutdownEnabled,
                )
            }
            setMaxNumberOfClients(maxClientLimit)
        }.build()
}
