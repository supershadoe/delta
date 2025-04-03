package dev.shadoe.delta.blocklist

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.shadoe.delta.api.ACLDevice
import dev.shadoe.delta.data.softap.BlocklistRepository
import javax.inject.Inject
import kotlinx.coroutines.ExperimentalCoroutinesApi

@HiltViewModel
class BlockListViewModel
@Inject
constructor(private val blocklistRepository: BlocklistRepository) :
  ViewModel() {
  @OptIn(ExperimentalCoroutinesApi::class)
  val blockedClients = blocklistRepository.blockedClients

  fun unblockDevice(device: ACLDevice) {
    blocklistRepository.unblockDevice(device)
  }
}
