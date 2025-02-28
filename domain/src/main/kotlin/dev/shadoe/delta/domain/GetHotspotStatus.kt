package dev.shadoe.delta.domain

import dev.shadoe.delta.data.softap.SoftApRepository
import javax.inject.Inject

class GetHotspotStatus
    @Inject
    constructor(
        private val softApRepository: SoftApRepository,
    ) {
        operator fun invoke() = softApRepository.status
    }
