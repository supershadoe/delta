package dev.shadoe.hotspotapi.callbacks

internal interface TetheringCallback {
    fun onOpSucceeded()

    fun onOpFailed(resultCode: Int)
}
