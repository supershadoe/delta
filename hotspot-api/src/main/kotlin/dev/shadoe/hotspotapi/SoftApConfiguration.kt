package dev.shadoe.hotspotapi

import android.net.MacAddress
import dev.shadoe.hotspotapi.helper.BlockedDevice
import dev.shadoe.hotspotapi.helper.SoftApSecurityType
import dev.shadoe.hotspotapi.helper.SoftApSpeedType

data class SoftApConfiguration(
    val ssid: String?,
    val passphrase: String,
    @SoftApSecurityType.SecurityType val securityType: Int,
    val bssid: MacAddress?,
    val isHidden: Boolean,
    @SoftApSpeedType.BandType val speedType: Int,
    val blockedDevices: List<BlockedDevice>,
    val isAutoShutdownEnabled: Boolean,
)
