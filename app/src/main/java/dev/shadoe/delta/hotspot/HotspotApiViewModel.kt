package dev.shadoe.delta.hotspot

import android.app.Application
import android.os.Build
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import dev.shadoe.hotspotapi.HotspotApi
import kotlinx.coroutines.launch

class HotspotApiViewModel(application: Application) :
    AndroidViewModel(application) {

    val hotspotApi = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        HotspotApi(
            application.packageName,
            application.attributionTag,
            application.attributionSource,
        )
    } else {
        HotspotApi(
            application.packageName,
            application.attributionTag,
        )
    }

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