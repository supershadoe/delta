package dev.shadoe.delta.domain

import dev.shadoe.delta.data.softap.SoftApRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.asStateFlow

class GetHotspotConfig
@Inject
constructor(private val softApRepository: SoftApRepository) {
  operator fun invoke() = softApRepository.config.asStateFlow()
}
