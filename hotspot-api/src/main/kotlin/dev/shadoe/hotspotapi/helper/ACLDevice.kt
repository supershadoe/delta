package dev.shadoe.hotspotapi.helper

import android.net.MacAddress

data class ACLDevice(
    val hostname: String?,
    val macAddress: MacAddress,
)
