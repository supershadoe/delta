package dev.shadoe.delta.domain

import dev.shadoe.delta.data.softap.SoftApRepository
import dev.shadoe.hotspotapi.wrappers.SoftApEnabledState
import javax.inject.Inject

class ControlHotspot
    @Inject
    constructor(
        private val softApRepository: SoftApRepository,
    ) {
        fun startHotspot(forceRestart: Boolean) {
            val enabledState = softApRepository.status.value.enabledState
            var shouldStart =
                enabledState == SoftApEnabledState.WIFI_AP_STATE_DISABLED
            if (forceRestart) {
                shouldStart =
                    enabledState == SoftApEnabledState.WIFI_AP_STATE_FAILED
            }
            if (!shouldStart) return
            softApRepository.startHotspot()
        }

        fun stopHotspot() {
            val state = softApRepository.status.value.enabledState
            if (state != SoftApEnabledState.WIFI_AP_STATE_ENABLED) {
                return
            }
            softApRepository.stopHotspot()
        }
    }
