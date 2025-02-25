package dev.shadoe.delta.hotspot.components

import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarVisuals

class HotspotNotEnabledSnackbar : SnackbarVisuals {
    override val message = "Hotspot is not yet enabled."
    override val actionLabel: String = "ENABLE"
    override val withDismissAction = true
    override val duration = SnackbarDuration.Short
}
