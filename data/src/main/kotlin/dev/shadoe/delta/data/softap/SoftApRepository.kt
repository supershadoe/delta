package dev.shadoe.delta.data.softap

import javax.inject.Inject
import kotlinx.coroutines.flow.asStateFlow

class SoftApRepository
@Inject
constructor(private val softApStateRepository: SoftApStateRepository) {
  val config = softApStateRepository.mConfig.asStateFlow()
  val status = softApStateRepository.mStatus.asStateFlow()
}
