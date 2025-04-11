package dev.shadoe.delta.data.softap.validators

import dev.shadoe.delta.api.SoftApSecurityType
import kotlin.test.Test
import kotlin.test.assertIs

class PassphraseValidatorTest {
  companion object {
    private const val SHORT_PASS = "short"
    private const val NORMAL_PASS = "this is a good passphrase"
    private const val LONG_PASS =
      "bruh max length is quite big for a normal human to exhaust the limit easily tbf."
  }

  @Test
  fun `When security type is WPA2-PSK or SAE-transition`() {
    val securityTypes =
      listOf(
        SoftApSecurityType.SECURITY_TYPE_WPA2_PSK,
        SoftApSecurityType.SECURITY_TYPE_WPA3_SAE_TRANSITION,
      )
    for (securityType in securityTypes) {
      val normal = PassphraseValidator.validate(NORMAL_PASS, securityType)
      val tooShort = PassphraseValidator.validate(SHORT_PASS, securityType)
      val tooLong = PassphraseValidator.validate(LONG_PASS, securityType)
      assertIs<PassphraseValidator.Result.Success>(normal)
      assertIs<PassphraseValidator.Result.PskTooShort>(tooShort)
      assertIs<PassphraseValidator.Result.PskTooLong>(tooLong)
    }
  }

  @Test
  fun `When security type is open`() {
    val securityType = SoftApSecurityType.SECURITY_TYPE_OPEN
    val results =
      listOf(
        PassphraseValidator.validate(NORMAL_PASS, securityType),
        PassphraseValidator.validate(SHORT_PASS, securityType),
        PassphraseValidator.validate(LONG_PASS, securityType),
        PassphraseValidator.validate("", securityType),
      )
    for (r in results) {
      assertIs<PassphraseValidator.Result.Success>(r)
    }
  }

  @Test
  fun `When security type is SAE and passphrase is not empty`() {
    val securityType = SoftApSecurityType.SECURITY_TYPE_WPA3_SAE
    val results =
      listOf(
        PassphraseValidator.validate(NORMAL_PASS, securityType),
        PassphraseValidator.validate(SHORT_PASS, securityType),
        PassphraseValidator.validate(LONG_PASS, securityType),
      )
    for (r in results) {
      assertIs<PassphraseValidator.Result.Success>(r)
    }
  }

  @Test
  fun `When security type is SAE and passphrase is empty`() {
    val emptyPass =
      PassphraseValidator.validate(
        "",
        SoftApSecurityType.SECURITY_TYPE_WPA3_SAE,
      )
    assertIs<PassphraseValidator.Result.PskTooShort>(emptyPass)
  }
}
