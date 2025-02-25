package dev.shadoe.hotspotapi.callbacks

import android.net.IIntResultListener
import android.net.TetheringManager.TETHER_ERROR_NO_ERROR
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

internal class TetheringResultListener(
    val callback: TetheringCallback,
) : IIntResultListener.Stub() {
    override fun onResult(resultCode: Int) {
        runBlocking {
            launch(Dispatchers.Unconfined) {
                if (resultCode == TETHER_ERROR_NO_ERROR) {
                    callback.onOpSucceeded()
                } else {
                    callback.onOpFailed(resultCode)
                }
            }
        }
    }
}
