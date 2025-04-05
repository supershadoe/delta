package dev.shadoe.delta.blocklist

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.shadoe.delta.api.ACLDevice
import dev.shadoe.delta.data.softap.SoftApBlocklistRepository
import javax.inject.Inject
import kotlinx.coroutines.ExperimentalCoroutinesApi

@HiltViewModel
class BlockListViewModel
@Inject
constructor(private val softApBlocklistRepository: SoftApBlocklistRepository) :
  ViewModel() {
  @OptIn(ExperimentalCoroutinesApi::class)
  val blockedClients = softApBlocklistRepository.blockedClients

  fun unblockDevice(device: ACLDevice) {
    softApBlocklistRepository.unblockDevice(device)
  }
}
