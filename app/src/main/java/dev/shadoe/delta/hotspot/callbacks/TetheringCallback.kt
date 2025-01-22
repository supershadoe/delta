package dev.shadoe.delta.hotspot.callbacks

internal interface TetheringCallback {
    fun onOpSucceeded()
    fun onOpFailed(resultCode: Int)
}