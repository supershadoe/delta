package dev.shadoe.delta.domain

import dev.shadoe.delta.data.softap.SoftApRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.mapLatest
import javax.inject.Inject

class ViewConnectedClients
    @Inject
    constructor(
        private val softApRepository: SoftApRepository,
    ) {
        @OptIn(ExperimentalCoroutinesApi::class)
        operator fun invoke() = softApRepository.status.mapLatest { it.tetheredClients }
    }
