package dev.shadoe.delta.api

import dev.shadoe.delta.api.SoftApSecurityType.SecurityType
import dev.shadoe.delta.api.SoftApSpeedType.BandType

data class SoftApCapabilities(
  val maxSupportedClients: Int,
  val clientForceDisconnectSupported: Boolean,
  val isMacAddressCustomizationSupported: Boolean,
  @BandType val supportedFrequencyBands: List<Int>,
  @SecurityType val supportedSecurityTypes: List<Int>,
  /** Map of band to list of supported channels. Empty list means all channels are supported. */
  val supportedChannels: Map<Int, List<Int>>,
)
