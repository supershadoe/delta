package dev.shadoe.delta.common

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.shadoe.delta.data.softap.SoftApStateStore
import javax.inject.Inject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.mapLatest

@HiltViewModel
class SoftApScreenViewModel @Inject constructor(state: SoftApStateStore) :
  ViewModel() {
  @OptIn(ExperimentalCoroutinesApi::class)
  val isSoftApSupported = state.status.mapLatest { it.isSoftApSupported }
}
