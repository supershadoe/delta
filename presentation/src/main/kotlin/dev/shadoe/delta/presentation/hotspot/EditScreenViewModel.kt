package dev.shadoe.delta.presentation.hotspot

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.shadoe.delta.domain.EditHotspotConfig
import dev.shadoe.delta.domain.GetHotspotConfig
import dev.shadoe.delta.domain.GetHotspotStatus
import dev.shadoe.delta.api.SoftApConfiguration
import javax.inject.Inject

@HiltViewModel
class EditScreenViewModel
    @Inject
    constructor(
        private val getHotspotConfig: GetHotspotConfig,
        private val getHotspotStatus: GetHotspotStatus,
        private val editHotspotConfig: EditHotspotConfig,
    ) : ViewModel() {
        val config
            get() = getHotspotConfig()

        val status
            get() = getHotspotStatus()

        fun updateConfig(config: SoftApConfiguration) {
            editHotspotConfig(config)
        }
    }
