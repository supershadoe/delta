package dev.shadoe.delta.api

import android.net.MacAddress

data class SoftApConfiguration(
    val ssid: String?,
    val passphrase: String,
    @SoftApSecurityType.SecurityType val securityType: Int,
    val bssid: MacAddress?,
    val isHidden: Boolean,
    @SoftApSpeedType.BandType val speedType: Int,
    val blockedDevices: List<ACLDevice>,
    val allowedClients: List<ACLDevice>,
    val isAutoShutdownEnabled: Boolean,
    val maxClientLimit: Int,
)
