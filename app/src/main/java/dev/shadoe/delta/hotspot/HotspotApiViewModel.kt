package dev.shadoe.delta.hotspot

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import dev.shadoe.hotspotapi.HotspotApi
import kotlinx.coroutines.launch

class HotspotApiViewModel(application: Application) :
    AndroidViewModel(application) {

    val hotspotApi = HotspotApi(
        application.packageName,
        application.attributionTag,
    )

    init {
        hotspotApi.registerCallback()
        viewModelScope.launch {
            hotspotApi.launchBackgroundTasks()
        }
    }

    override fun onCleared() {
        hotspotApi.unregisterCallback()
        super.onCleared()
    }
}