package dev.shadoe.hotspotapi

class TetheringExceptions private constructor() {
    class BinderAcquisitionException(message: String) : Exception(message)
}
