package dev.shadoe.delta.data.softap.internal

import dev.shadoe.delta.api.SoftApCapabilities
import dev.shadoe.delta.api.TetheredClient

internal interface TetheringEventListener {
  /**
   * Callback to update the state of Soft AP.
   *
   * Fetch the latest state in the callback using
   * [android.net.wifi.IWifiManager.getWifiApEnabledState]
   */
  fun onEnabledStateChanged()

  /** Callback to update the list of tethered clients. */
  fun onTetheredClientsChanged(clients: List<TetheredClient>)

  /**
   * Callback to update the soft AP capabilities obtained from the device
   * networking driver.
   *
   * This value should almost never change after start.
   */
  fun onSoftApCapabilitiesChanged(capabilities: SoftApCapabilities)
}
