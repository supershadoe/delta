package dev.shadoe.delta.navigation

import kotlinx.serialization.Serializable

class Routes {
  @Serializable object FirstUseScreen

  @Serializable object HotspotScreen

  @Serializable object HotspotEditScreen

  @Serializable object BlocklistScreen

  @Serializable object DebugScreen
}
