package dev.shadoe.hotspotapi.callbacks

import android.net.wifi.ISoftApCallback
import android.net.wifi.SoftApCapability
import android.net.wifi.SoftApInfo
import android.net.wifi.SoftApState
import android.net.wifi.WifiClient

class SoftApCallback: ISoftApCallback.Stub() {
    override fun onStateChanged(state: SoftApState?) {
//        TODO("Not yet implemented")
        println("soft ap state changed $state")
    }

    override fun onConnectedClientsOrInfoChanged(
        infos: Map<String?, SoftApInfo?>?,
        clients: Map<String?, List<WifiClient?>?>?,
        isBridged: Boolean,
        isRegistration: Boolean
    ) {
//        TODO("Not yet implemented")
        println("soft ap info changed $infos")
        println("soft ap clients changed $clients")
        println("soft ap change isRegistration = $isRegistration isBridged = $isBridged")
    }

    override fun onCapabilityChanged(capability: SoftApCapability?) {
//        TODO("Not yet implemented")
        println("soft ap cap changed $capability")
    }

    /**
     * This function is only called by Android when a device is not in allow
     * or block list when `SoftApConfiguration.Builder.setClientControlByUserEnabled(true)`
     * is set, else blocked devices are silently blocked
     * The devices in block list aren't sent via this callback, only devices that
     * aren't in either list are sent for the purpose of updating our ACL
     *
     * Also, this callback is called when maximum connection limit is reached.
     * TODO: think about how to use this in the going forward.
     */
    override fun onBlockedClientConnecting(
        client: WifiClient?,
        blockedReason: Int
    ) {
//        TODO("Not yet implemented")
        println("blocked client ${client?.macAddress} $blockedReason")
    }
}