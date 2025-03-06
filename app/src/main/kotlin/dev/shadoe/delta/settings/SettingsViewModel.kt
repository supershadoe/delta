package dev.shadoe.delta.settings

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.shadoe.delta.api.ACLDevice
import dev.shadoe.delta.api.SoftApConfiguration
import dev.shadoe.delta.data.softap.SoftApRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.mapLatest
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel
@Inject
constructor(private val softApRepository: SoftApRepository) : ViewModel() {
  val config = softApRepository.config

  val status = softApRepository.status

  @OptIn(ExperimentalCoroutinesApi::class)
  val blockedClients = softApRepository.config.mapLatest { it.blockedDevices }

  fun updateConfig(config: SoftApConfiguration) {
    softApRepository.config.value = config
  }
  fun unblockDevice(device: ACLDevice) {
    softApRepository.apply {
      val d = config.value.blockedDevices
      config.value = config.value.copy(blockedDevices = d - device)
    }
  }
}
