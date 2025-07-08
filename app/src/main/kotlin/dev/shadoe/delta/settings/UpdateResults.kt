package dev.shadoe.delta.settings

import dev.shadoe.delta.data.softap.validators.PassphraseValidator
import dev.shadoe.delta.data.softap.validators.SsidValidator

data class UpdateResults(
  val ssidResult: SsidValidator.Result = SsidValidator.Result.Success,
  val passphraseResult: PassphraseValidator.Result =
    PassphraseValidator.Result.Success,
  val isMaxClientLimitEmpty: Boolean = false,
)
