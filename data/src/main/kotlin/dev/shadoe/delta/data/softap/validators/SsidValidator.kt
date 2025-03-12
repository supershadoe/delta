package dev.shadoe.delta.data.softap.validators

object SsidValidator {
  const val SSID_ASCII_MIN_LENGTH = 1
  const val SSID_ASCII_MAX_LENGTH = 32

  sealed class Result {
    object Success : Result()

    object SsidTooShort : Result()

    object SsidTooLong : Result()
  }

  fun validate(ssid: String): Result =
    when {
      ssid.length < SSID_ASCII_MIN_LENGTH -> Result.SsidTooShort
      ssid.encodeToByteArray().size > SSID_ASCII_MAX_LENGTH ->
        Result.SsidTooLong
      else -> Result.Success
    }
}
