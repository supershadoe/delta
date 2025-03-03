package dev.shadoe.delta.domain

import javax.inject.Inject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.mapLatest

class ViewConnectedClients
@Inject
constructor(private val getHotspotStatus: GetHotspotStatus) {
  @OptIn(ExperimentalCoroutinesApi::class)
  operator fun invoke() = getHotspotStatus().mapLatest { it.tetheredClients }
}
