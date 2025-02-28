package dev.shadoe.delta.api

import android.net.LinkAddress
import android.net.MacAddress

data class TetheredClient(
    val macAddress: MacAddress,
    val address: LinkAddress?,
    val hostname: String?,
    val tetheringType: Int,
)
