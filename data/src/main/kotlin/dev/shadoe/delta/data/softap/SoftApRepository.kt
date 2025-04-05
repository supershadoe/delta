package dev.shadoe.delta.data.softap

import dev.shadoe.delta.api.SoftApConfiguration
import javax.inject.Inject
import kotlinx.coroutines.flow.asStateFlow

class SoftApRepository
@Inject
constructor(private val softApStateRepository: SoftApStateRepository) {
  val config = softApStateRepository.config.asStateFlow()
  val status = softApStateRepository.status.asStateFlow()

  fun updateSoftApConfiguration(c: SoftApConfiguration): Boolean =
    softApStateRepository.updateSoftApConfiguration(c)
}
