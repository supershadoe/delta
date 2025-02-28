package dev.shadoe.delta.domain

import dev.shadoe.delta.data.softap.SoftApRepository
import javax.inject.Inject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.mapLatest

class ViewConnectedClients
@Inject
constructor(private val softApRepository: SoftApRepository) {
  @OptIn(ExperimentalCoroutinesApi::class)
  operator fun invoke() =
    softApRepository.status.mapLatest { it.tetheredClients }
}
