package dev.shadoe.delta.presentation.hotspot

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.shadoe.delta.data.softap.SoftApRepository
import javax.inject.Inject

@HiltViewModel
class HotspotScopeViewModel
    @Inject
    constructor(
        private val softApRepository: SoftApRepository,
    ) : ViewModel() {
        init {
            softApRepository.onCreate(viewModelScope)
        }

        override fun onCleared() {
            softApRepository.onDestroy()
            super.onCleared()
        }
    }
