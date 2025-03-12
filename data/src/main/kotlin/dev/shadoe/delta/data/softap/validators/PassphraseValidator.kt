package dev.shadoe.delta.data.softap.validators

import dev.shadoe.delta.api.SoftApSecurityType
import dev.shadoe.delta.api.SoftApSecurityType.SecurityType

object PassphraseValidator {
  const val PSK_PASSPHRASE_ASCII_MIN_LENGTH = 8
  const val PSK_PASSPHRASE_ASCII_MAX_LENGTH = 63

  sealed class Result {
    object Success : Result()

    object PskTooShort : Result()

    object PskTooLong : Result()
  }

  fun validate(passphrase: String, @SecurityType securityType: Int): Result {
    val usesPsk =
      securityType == SoftApSecurityType.SECURITY_TYPE_WPA2_PSK ||
        securityType == SoftApSecurityType.SECURITY_TYPE_WPA3_SAE_TRANSITION
    if (!usesPsk) return Result.Success
    return when {
      passphrase.length < PSK_PASSPHRASE_ASCII_MIN_LENGTH -> Result.PskTooShort
      passphrase.length > PSK_PASSPHRASE_ASCII_MAX_LENGTH -> Result.PskTooLong
      else -> Result.Success
    }
  }
}
