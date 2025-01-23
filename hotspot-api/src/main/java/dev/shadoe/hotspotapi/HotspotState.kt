package dev.shadoe.hotspotapi

import android.net.MacAddress
import android.net.TetheredClient
import kotlinx.coroutines.flow.MutableStateFlow

internal class HotspotState internal constructor(
    enabledState: Int = WifiApEnabledStates.WIFI_AP_STATE_DISABLED,
    ssid: String? = null,
    passphrase: String? = null,
    securityType: Int = WifiSecurityTypes.SECURITY_TYPE_WPA2_PSK,
    bssid: MacAddress? = null,
    isHidden: Boolean = false,
    tetheredClients: List<TetheredClient> = emptyList(),
) {

    companion object {
        internal var instance: HotspotState? = null
    }

    val enabledState = MutableStateFlow(enabledState)
    var ssid = MutableStateFlow(ssid)
    var passphrase = MutableStateFlow(passphrase)
    val securityType = MutableStateFlow(securityType)
    var bssid = MutableStateFlow(bssid)
    var isHidden = MutableStateFlow(isHidden)
    val tetheredClients = MutableStateFlow(tetheredClients)
}