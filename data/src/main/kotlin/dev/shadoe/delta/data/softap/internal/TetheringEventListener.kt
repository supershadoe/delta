package dev.shadoe.delta.data.softap.internal

import dev.shadoe.delta.api.SoftApSpeedType.BandType
import dev.shadoe.delta.api.TetheredClient

internal interface TetheringEventListener {
    /**
     * Callback to update the state of Soft AP.
     *
     * Fetch the latest state in the callback using
     * [android.net.wifi.IWifiManager.getWifiApEnabledState]
     */
    fun onEnabledStateChanged()

    /**
     * Callback to update the list of tethered clients.
     */
    fun onTetheredClientsChanged(clients: List<TetheredClient>)

    /**
     * Callback to update the frequency bands supported by the
     * device networking driver.
     *
     * Single-use callback, this value should almost never change
     * after start.
     */
    fun onSupportedFrequencyBandsChanged(
        @BandType frequencyBands: List<Int>,
    )

    /**
     * Callback to update the maximum number of clients supported by
     * the device networking driver.
     *
     * Single-use callback, this value should almost never change
     * after start.
     */
    fun onMaxClientLimitChanged(maxClients: Int)
}
