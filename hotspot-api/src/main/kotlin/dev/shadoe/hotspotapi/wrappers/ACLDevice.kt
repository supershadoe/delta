package dev.shadoe.hotspotapi.wrappers

import android.net.MacAddress

data class ACLDevice(
    val hostname: String?,
    val macAddress: MacAddress,
)
