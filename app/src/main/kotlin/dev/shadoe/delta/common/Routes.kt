package dev.shadoe.delta.common

import kotlinx.serialization.Serializable

object Routes {
  @Serializable
  object Setup {
    @Serializable object FirstUseScreen

    @Serializable object ShizukuSetupScreen

    @Serializable object CrashHandlerSetupScreen
  }

  @Serializable object HotspotScreen

  @Serializable object HotspotEditScreen

  @Serializable object BlocklistScreen

  @Serializable object DebugScreen
}
