package dev.shadoe.delta.hotspot

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import dev.shadoe.hotspotapi.HotspotApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HotspotApiViewModel(
    application: Application,
) : AndroidViewModel(application) {
    val hotspotApi =
        HotspotApi(
            applicationContext = application.applicationContext,
        )

    init {
        viewModelScope.launch {
            withContext(Dispatchers.Unconfined) {
                hotspotApi.startBackgroundJobs(this)
            }
        }
    }

    override fun onCleared() {
        hotspotApi.cleanUp()
        super.onCleared()
    }
}
