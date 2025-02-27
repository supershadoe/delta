package dev.shadoe.delta.hotspot

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import dev.shadoe.hotspotapi.HotspotApi

class HotspotApiViewModel(
    application: Application,
) : AndroidViewModel(application) {
    val hotspotApi =
        HotspotApi(
            applicationContext = application.applicationContext,
            scope = viewModelScope,
        )

    override fun onCleared() {
        hotspotApi.cleanUp()
        super.onCleared()
    }
}
