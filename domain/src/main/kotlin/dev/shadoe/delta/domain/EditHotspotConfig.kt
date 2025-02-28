package dev.shadoe.delta.domain

import dev.shadoe.delta.data.softap.SoftApRepository
import dev.shadoe.delta.api.SoftApConfiguration
import kotlinx.coroutines.flow.update
import javax.inject.Inject

class EditHotspotConfig
    @Inject
    constructor(
        private val softApRepository: SoftApRepository,
    ) {
        operator fun invoke(config: SoftApConfiguration) {
            if (softApRepository.updateSoftApConfiguration(config)) {
                softApRepository.config.update { config }
            }
        }
    }
