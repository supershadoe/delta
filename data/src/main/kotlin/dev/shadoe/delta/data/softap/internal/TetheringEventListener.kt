package dev.shadoe.delta.data.softap.internal

import dev.shadoe.delta.api.SoftApCapabilities
import dev.shadoe.delta.api.SoftApEnabledState.EnabledStateType
import dev.shadoe.delta.api.TetheredClient

internal interface TetheringEventListener {
  /** Callback to update the state of Soft AP. */
  fun onEnabledStateChanged(@EnabledStateType state: Int)

  /** Callback to update the list of tethered clients. */
  fun onTetheredClientsChanged(clients: List<TetheredClient>)

  /**
   * Callback to update the soft AP capabilities obtained from the device
   * networking driver.
   *
   * This value should almost never change after start.
   */
  fun onSoftApCapabilitiesChanged(capabilities: SoftApCapabilities)

  /** Callback that indicates whether the device supports Soft AP. */
  fun onSoftApSupported(isSupported: Boolean)
}
