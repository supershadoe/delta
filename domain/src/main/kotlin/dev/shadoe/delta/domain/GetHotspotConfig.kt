package dev.shadoe.delta.domain

import dev.shadoe.delta.data.softap.SoftApRepository
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

class GetHotspotConfig
    @Inject
    constructor(
        private val softApRepository: SoftApRepository,
    ) {
        operator fun invoke() = softApRepository.config.asStateFlow()
    }
