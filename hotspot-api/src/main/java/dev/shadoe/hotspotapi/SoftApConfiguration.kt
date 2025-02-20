package dev.shadoe.hotspotapi

import android.net.MacAddress

data class SoftApConfiguration(
    val ssid: String?,
    val passphrase: String?,
    @SoftApSecurityType.SecurityType val securityType: Int,
    val bssid: MacAddress?,
    val isHidden: Boolean,
    @SoftApSpeedType.BandType val speedType: Int,
    val blockedDevices: List<MacAddress>,
    val isAutoShutdownEnabled: Boolean,
)