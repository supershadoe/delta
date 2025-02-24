package dev.shadoe.hotspotapi.callbacks

import android.net.TetheringManager.StartTetheringError

internal class StartTetheringCallback : TetheringCallback {
    override fun onOpSucceeded() {
//        TODO("Not yet implemented")
        println("tethering started")
    }

    override fun onOpFailed(
        @StartTetheringError resultCode: Int,
    ) {
//        TODO("Not yet implemented")
        println("tethering failed due to $resultCode")
    }
}
