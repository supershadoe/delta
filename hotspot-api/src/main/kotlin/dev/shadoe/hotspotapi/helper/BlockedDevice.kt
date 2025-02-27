package dev.shadoe.hotspotapi.helper

import android.net.MacAddress

data class BlockedDevice(
    val hostname: String?,
    val macAddress: MacAddress,
)
