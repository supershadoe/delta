package dev.shadoe.delta.debug

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.shadoe.delta.data.softap.SoftApStateRepository
import javax.inject.Inject

@HiltViewModel
class DebugViewModel
@Inject
constructor(softApStateRepository: SoftApStateRepository) : ViewModel() {
  val config = softApStateRepository.config
  val status = softApStateRepository.status
}
