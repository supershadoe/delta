package dev.shadoe.delta.debug

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.shadoe.delta.api.ConfigFlag
import dev.shadoe.delta.data.FlagsRepository
import dev.shadoe.delta.data.MacAddressCacheRepository
import dev.shadoe.delta.data.softap.SoftApStateStore
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@HiltViewModel
class DebugViewModel
@Inject
constructor(
  softApStateStore: SoftApStateStore,
  private val flagsRepository: FlagsRepository,
  private val macAddressCacheRepository: MacAddressCacheRepository,
) : ViewModel() {
  val config = softApStateStore.config
  val status = softApStateStore.status
  val flags = MutableStateFlow("")
  val macAddressCache = MutableStateFlow("")

  init {
    viewModelScope.launch {
      withContext(Dispatchers.IO) {
        flags.value =
          flagsRepository
            .debugDumpFlags()
            .map { ConfigFlag.entries[it.flag].name to it.value }
            .toString()
        macAddressCache.value =
          macAddressCacheRepository.debugDumpCache().toString()
      }
    }
  }
}
