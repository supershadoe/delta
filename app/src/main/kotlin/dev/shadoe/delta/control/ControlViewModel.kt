package dev.shadoe.delta.control

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.shadoe.delta.api.SoftApSecurityType
import dev.shadoe.delta.data.softap.SoftApRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.mapLatest
import javax.inject.Inject

@HiltViewModel
class ControlViewModel
@Inject
constructor(private val softApRepository: SoftApRepository) : ViewModel() {
  fun startHotspot(forceRestart: Boolean = false) =
    softApRepository.startHotspot(forceRestart)

  fun stopHotspot() = softApRepository.stopHotspot()

  @OptIn(ExperimentalCoroutinesApi::class)
  val ssid
    get() = softApRepository.config.mapLatest { it.ssid }

  @OptIn(ExperimentalCoroutinesApi::class)
  val passphrase
    get() = softApRepository.config.mapLatest { it.passphrase }

  @OptIn(ExperimentalCoroutinesApi::class)
  val shouldShowPassphrase =
    softApRepository.config.mapLatest {
      it.securityType != SoftApSecurityType.SECURITY_TYPE_OPEN
    }

  @OptIn(ExperimentalCoroutinesApi::class)
  val enabledState
    get() = softApRepository.status.mapLatest { it.enabledState }

  @OptIn(ExperimentalCoroutinesApi::class)
  val tetheredClientCount
    get() = softApRepository.status.mapLatest { it.tetheredClients.size }
}
