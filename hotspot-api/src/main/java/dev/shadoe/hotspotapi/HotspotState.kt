package dev.shadoe.hotspotapi

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

internal class HotspotState(initialState: Int) {
    private val _enabledState: MutableStateFlow<Int> = MutableStateFlow(initialState)
    val enabledState: StateFlow<Int> = _enabledState
}