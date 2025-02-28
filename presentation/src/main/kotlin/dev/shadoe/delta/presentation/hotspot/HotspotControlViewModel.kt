package dev.shadoe.delta.presentation.hotspot

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.shadoe.delta.api.SoftApSecurityType
import dev.shadoe.delta.domain.ControlHotspot
import dev.shadoe.delta.domain.GetHotspotConfig
import dev.shadoe.delta.domain.GetHotspotStatus
import dev.shadoe.delta.domain.ViewConnectedClients
import javax.inject.Inject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.mapLatest

@HiltViewModel
class HotspotControlViewModel
@Inject
constructor(
  private val controlHotspot: ControlHotspot,
  private val getHotspotConfig: GetHotspotConfig,
  private val getHotspotStatus: GetHotspotStatus,
  private val viewConnectedClients: ViewConnectedClients,
) : ViewModel() {
  fun startHotspot(forceRestart: Boolean = false) =
    controlHotspot.startHotspot(forceRestart)

  fun stopHotspot() = controlHotspot.stopHotspot()

  @OptIn(ExperimentalCoroutinesApi::class)
  val ssid
    get() = getHotspotConfig().mapLatest { it.ssid }

  @OptIn(ExperimentalCoroutinesApi::class)
  val passphrase
    get() = getHotspotConfig().mapLatest { it.passphrase }

  @OptIn(ExperimentalCoroutinesApi::class)
  val shouldShowPassphrase =
    getHotspotConfig().mapLatest {
      it.securityType != SoftApSecurityType.SECURITY_TYPE_OPEN
    }

  @OptIn(ExperimentalCoroutinesApi::class)
  val enabledState
    get() = getHotspotStatus().mapLatest { it.enabledState }

  @OptIn(ExperimentalCoroutinesApi::class)
  val tetheredClientCount
    get() = viewConnectedClients().mapLatest { it.size }
}
