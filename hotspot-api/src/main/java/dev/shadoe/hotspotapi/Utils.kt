package dev.shadoe.hotspotapi

object Utils {
    infix fun Int.hasBit(other: Int): Boolean = (this and other) == other
    infix fun Long.hasBit(other: Long): Boolean = (this and other) == other
}

