package dev.shadoe.delta.api

data class TetheredClient(
  val macAddress: MacAddress,
  val address: LinkAddress?,
  val hostname: String?,
  val tetheringType: Int,
)
