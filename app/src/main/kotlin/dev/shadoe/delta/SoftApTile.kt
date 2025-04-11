package dev.shadoe.delta

import android.service.quicksettings.Tile
import android.service.quicksettings.TileService
import dagger.hilt.android.AndroidEntryPoint
import dev.shadoe.delta.api.SoftApEnabledState
import dev.shadoe.delta.data.softap.SoftApControlRepository
import dev.shadoe.delta.data.softap.SoftApStateRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Inject

// TODO (supershadoe): move strings to xml
// TODO (supershadoe): add shizuku repo and check for shizuku state before any op
// TODO (supershadoe): make soft ap state repo not depend on WifiManager

@AndroidEntryPoint
class SoftApTile : TileService() {
  private val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())
  @Inject lateinit var softApControlRepository: SoftApControlRepository
  @Inject lateinit var softApStateRepository: SoftApStateRepository

  override fun onCreate() {
    super.onCreate()
    scope.launch {
      softApStateRepository.status.collect {
        qsTile?.apply {
          state = when (it.enabledState) {
            SoftApEnabledState.WIFI_AP_STATE_DISABLING -> Tile.STATE_UNAVAILABLE
            SoftApEnabledState.WIFI_AP_STATE_DISABLED -> Tile.STATE_INACTIVE
            SoftApEnabledState.WIFI_AP_STATE_ENABLING -> Tile.STATE_UNAVAILABLE
            SoftApEnabledState.WIFI_AP_STATE_ENABLED -> Tile.STATE_ACTIVE
            SoftApEnabledState.WIFI_AP_STATE_FAILED -> Tile.STATE_UNAVAILABLE
            else -> Tile.STATE_INACTIVE
          }
          subtitle = if (it.enabledState == SoftApEnabledState.WIFI_AP_STATE_ENABLED) "${it.tetheredClients.size} devices" else "Turned off"
          updateTile()
        }
      }
    }
  }

  override fun onStartListening() {
    super.onStartListening()
    qsTile?.apply {
      label = "Hotspot"
      updateTile()
    }
  }

  override fun onClick() {
    super.onClick()
    when (qsTile.state) {
      Tile.STATE_INACTIVE -> softApControlRepository.startSoftAp()
      Tile.STATE_ACTIVE -> softApControlRepository.stopSoftAp()
      Tile.STATE_UNAVAILABLE -> {}
    }
  }
}
