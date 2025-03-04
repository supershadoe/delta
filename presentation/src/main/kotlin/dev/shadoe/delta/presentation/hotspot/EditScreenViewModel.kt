package dev.shadoe.delta.presentation.hotspot

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.shadoe.delta.api.SoftApConfiguration
import dev.shadoe.delta.data.softap.SoftApRepository
import javax.inject.Inject

@HiltViewModel
class EditScreenViewModel
@Inject
constructor(private val softApRepository: SoftApRepository) : ViewModel() {
  val config = softApRepository.config

  val status = softApRepository.status

  fun updateConfig(config: SoftApConfiguration) {
    softApRepository.config.value = config
  }
}
