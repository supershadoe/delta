package dev.shadoe.hotspotapi.callbacks

import android.net.wifi.ISoftApCallback
import android.net.wifi.SoftApCapability
import android.net.wifi.SoftApInfo
import android.net.wifi.SoftApState
import android.net.wifi.WifiClient
import dev.shadoe.hotspotapi.helper.SoftApSpeedType

class SoftApCallback(
    private val setSupportedSpeedTypes: (List<Int>) -> Unit,
    private val setMaxClientLimit: (Int) -> Unit,
) : ISoftApCallback.Stub() {
    /**
     * Results in a no-op because already [TetheringEventCallback] handles it
     */
    override fun onStateChanged(state: SoftApState?) {}

    /**
     * Results in a no-op because already [TetheringEventCallback] handles it
     */
    override fun onConnectedClientsOrInfoChanged(
        infos: Map<String?, SoftApInfo?>?,
        clients: Map<String?, List<WifiClient?>?>?,
        isBridged: Boolean,
        isRegistration: Boolean,
    ) {}

    /**
     * Gets supported bands, max client limit and other capabilities and
     * update state.
     */
    override fun onCapabilityChanged(capability: SoftApCapability?) {
        capability ?: return
        println("soft ap cap changed $capability")
        updateSupportedSpeedTypes(capability)
        setMaxClientLimit(capability.maxSupportedClients)
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
        blockedReason: Int,
    ) {
        println("blocked client ${client?.macAddress} $blockedReason")
    }

    private fun updateSupportedSpeedTypes(capability: SoftApCapability) {
        val bandToSoftApFeatureMap =
            mapOf(
                SoftApSpeedType.BAND_2GHZ to SoftApCapability.SOFTAP_FEATURE_BAND_24G_SUPPORTED,
                SoftApSpeedType.BAND_5GHZ to SoftApCapability.SOFTAP_FEATURE_BAND_5G_SUPPORTED,
                SoftApSpeedType.BAND_6GHZ to SoftApCapability.SOFTAP_FEATURE_BAND_6G_SUPPORTED,
            )

        bandToSoftApFeatureMap.keys
            .filter { band: Int ->
                val isSupported =
                    capability.areFeaturesSupported(
                        bandToSoftApFeatureMap.getValue(band),
                    )
                val isAvailable =
                    capability.getSupportedChannelList(band).isNotEmpty()
                isSupported && isAvailable
            }.let { setSupportedSpeedTypes(it) }
    }
}
