package dev.shadoe.hotspotapi.helper

import android.net.LinkAddress
import android.net.MacAddress
import android.net.TetheredClient
import kotlin.collections.List

/**
 * A wrapper class to wrap [TetheredClient] to let
 * the app use the data without needing to compile with the stubs which
 * makes the app layer kinda messy.
 */
class TetheredClientWrapper(
    c: TetheredClient,
) {
    val macAddress: MacAddress = c.macAddress
    val addresses: List<LinkAddress?>
    val hostnames: List<String?>
    val tetheringType: Int

    init {
        val addresses = mutableListOf<LinkAddress?>()
        val hostnames = mutableListOf<String?>()
        this.tetheringType = c.tetheringType

        for (address in c.addresses) {
            println(address)
            addresses.add(address.address)
            hostnames.add(address.hostname)
        }

        this.addresses = addresses.toList()
        this.hostnames = hostnames.toList()
    }
}
