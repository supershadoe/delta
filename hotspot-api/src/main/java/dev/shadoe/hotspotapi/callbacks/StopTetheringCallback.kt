package dev.shadoe.hotspotapi.callbacks

import android.net.TetheringManager.StopTetheringError

internal class StopTetheringCallback : TetheringCallback {
    override fun onOpSucceeded() {
//        TODO("Not yet implemented")
        println("tethering stopped")
    }

    override fun onOpFailed(@StopTetheringError resultCode: Int) {
//        TODO("Not yet implemented")
        println("tethering failed due to $resultCode")
    }
}