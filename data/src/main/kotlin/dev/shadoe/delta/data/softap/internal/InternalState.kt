package dev.shadoe.delta.data.softap.internal

internal data class InternalState(
  val fallbackPassphrase: String = "",
  val shouldRestart: Boolean = false,
)
