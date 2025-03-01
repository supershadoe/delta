package dev.shadoe.delta.domain

import dev.shadoe.delta.api.SoftApConfiguration
import dev.shadoe.delta.data.softap.SoftApRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.update

class EditHotspotConfig
@Inject
constructor(private val softApRepository: SoftApRepository) {
  operator fun invoke(config: SoftApConfiguration): Boolean {
    val shouldUpdate = softApRepository.updateSoftApConfiguration(config)
    if (shouldUpdate) {
      softApRepository.config.update { config }
    }
    return shouldUpdate
  }
}
