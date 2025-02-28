package dev.shadoe.delta.domain

import dev.shadoe.delta.api.ACLDevice
import dev.shadoe.delta.data.softap.SoftApRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.update
import javax.inject.Inject

class UseBlockList
    @Inject
    constructor(
        private val softApRepository: SoftApRepository,
    ) {
        @OptIn(ExperimentalCoroutinesApi::class)
        fun getBlockedClientsFlow(): Flow<List<ACLDevice>> =
            softApRepository.config.mapLatest {
                it.blockedDevices
            }

        fun blockClient(device: ACLDevice) = softApRepository.config.update {
            it.copy(
                blockedDevices = it.blockedDevices + device,
            )
        }

        fun unblockClient(device: ACLDevice) = softApRepository.config.update {
            it.copy(
                blockedDevices = it.blockedDevices - device,
            )
        }
    }
