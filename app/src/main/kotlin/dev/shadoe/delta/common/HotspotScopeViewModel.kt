package dev.shadoe.delta.common

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.shadoe.delta.data.softap.SoftApRepository
import javax.inject.Inject

@HiltViewModel
class HotspotScopeViewModel @Inject constructor(repo: SoftApRepository) :
  ViewModel() {
  init {
    addCloseable(repo.viewModelHook(viewModelScope))
  }
}
