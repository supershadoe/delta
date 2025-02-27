package dev.shadoe.hotspotapi.internal

import android.net.MacAddress
import dev.shadoe.hotspotapi.Utils.generateRandomPassword

internal data class InternalState(
    val fallbackPassphrase: String = generateRandomPassword(),
    val macAddressCache: Map<MacAddress, String> = emptyMap(),
)
