package dev.shadoe.delta.common

import kotlinx.serialization.Serializable

sealed interface Route {}

object Routes {
  @Serializable object BlankScreen : Route

  @Serializable
  object Setup {
    @Serializable object FirstUseScreen : Route

    @Serializable object ShizukuSetupScreen : Route

    @Serializable object CrashHandlerSetupScreen : Route
  }

  @Serializable object HotspotScreen : Route

  @Serializable object HotspotEditScreen : Route

  @Serializable object BlocklistScreen : Route

  @Serializable object DebugScreen : Route
}
