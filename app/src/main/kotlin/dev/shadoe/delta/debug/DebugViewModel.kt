package dev.shadoe.delta.debug

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.shadoe.delta.data.softap.SoftApRepository
import javax.inject.Inject

@HiltViewModel
class DebugViewModel @Inject constructor(softApRepository: SoftApRepository) :
  ViewModel() {
  val config = softApRepository.config
  val status = softApRepository.status
}
