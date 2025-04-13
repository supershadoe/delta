package dev.shadoe.delta

import android.service.quicksettings.Tile
import android.service.quicksettings.TileService
import dagger.hilt.android.AndroidEntryPoint
import dev.shadoe.delta.api.ShizukuStates
import dev.shadoe.delta.api.SoftApEnabledState
import dev.shadoe.delta.api.SoftApStatus
import dev.shadoe.delta.data.shizuku.ShizukuRepository
import dev.shadoe.delta.data.softap.SoftApControlRepository
import dev.shadoe.delta.data.softap.SoftApStateFacade
import javax.inject.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SoftApTile : TileService() {
  private var job: Job? = null

  private fun updateTileInfo(
    @ShizukuStates.ShizukuStateType shizukuState: Int,
    softApStatus: SoftApStatus,
  ) {
    if (shizukuState != ShizukuStates.CONNECTED) {
      qsTile?.apply {
        state = Tile.STATE_UNAVAILABLE
        subtitle = getString(R.string.tile_disabled)
        updateTile()
      }
      return
    }
    qsTile?.apply {
      state =
        when (softApStatus.enabledState) {
          SoftApEnabledState.WIFI_AP_STATE_DISABLING -> Tile.STATE_UNAVAILABLE
          SoftApEnabledState.WIFI_AP_STATE_DISABLED -> Tile.STATE_INACTIVE
          SoftApEnabledState.WIFI_AP_STATE_ENABLING -> Tile.STATE_UNAVAILABLE
          SoftApEnabledState.WIFI_AP_STATE_ENABLED -> Tile.STATE_ACTIVE
          SoftApEnabledState.WIFI_AP_STATE_FAILED -> Tile.STATE_UNAVAILABLE
          else -> Tile.STATE_INACTIVE
        }
      subtitle =
        when (softApStatus.enabledState) {
          SoftApEnabledState.WIFI_AP_STATE_DISABLING ->
            getString(R.string.tile_disabling)
          SoftApEnabledState.WIFI_AP_STATE_DISABLED ->
            getString(R.string.tile_disabled)
          SoftApEnabledState.WIFI_AP_STATE_ENABLING ->
            getString(R.string.tile_enabling)
          SoftApEnabledState.WIFI_AP_STATE_ENABLED ->
            getString(R.string.tile_enabled, softApStatus.tetheredClients.size)
          SoftApEnabledState.WIFI_AP_STATE_FAILED ->
            getString(R.string.tile_failed)
          else -> getString(R.string.tile_failed)
        }
      updateTile()
    }
  }

  @Inject lateinit var shizukuRepository: ShizukuRepository
  @Inject lateinit var softApControlRepository: SoftApControlRepository
  @Inject lateinit var softApStateFacade: SoftApStateFacade

  override fun onStartListening() {
    super.onStartListening()
    job?.cancel()
    job =
      CoroutineScope(Dispatchers.Default).launch {
        combine(shizukuRepository.shizukuState, softApStateFacade.status) {
            p0,
            p1 ->
            p0 to p1
          }
          .distinctUntilChanged()
          .collectLatest { (shizukuState, softApState) ->
            if (shizukuState == ShizukuStates.CONNECTED) {
              softApStateFacade.start()
            } else {
              softApStateFacade.stop()
            }
            updateTileInfo(shizukuState, softApState)
          }
      }
  }

  override fun onStopListening() {
    job?.cancel()
    super.onStopListening()
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
