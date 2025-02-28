package dev.shadoe.delta.presentation.shizuku

import androidx.annotation.IntDef

object ShizukuStates {
    const val NOT_READY = -1
    const val NOT_AVAILABLE = 0
    const val NOT_RUNNING = 1
    const val NOT_CONNECTED = 2
    const val CONNECTED = 3

    @IntDef(
        value = [
            NOT_READY,
            NOT_AVAILABLE,
            NOT_RUNNING,
            NOT_CONNECTED,
            CONNECTED,
        ],
    )
    annotation class ShizukuStateType
}
