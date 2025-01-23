package dev.shadoe.delta.hotspot

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import dev.shadoe.hotspotapi.HotspotApi

class HotspotApiViewModel(application: Application) :
    AndroidViewModel(application) {

    val hotspotApi =
        HotspotApi(application.packageName, application.attributionTag)

    init {
        hotspotApi.registerCallback()
    }

    override fun onCleared() {
        hotspotApi.unregisterCallback()
        super.onCleared()
    }
}