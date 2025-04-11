package dev.shadoe.delta.data.softap.validators

import kotlin.test.Test
import kotlin.test.assertIs

class SsidValidatorTest {
  @Test
  fun `Validation of ASCII SSIDs`() {
    val normal = SsidValidator.validate("test")
    val tooShort = SsidValidator.validate("")
    val tooLong = SsidValidator.validate("a".repeat(33))
    assertIs<SsidValidator.Result.Success>(normal)
    assertIs<SsidValidator.Result.SsidTooShort>(tooShort)
    assertIs<SsidValidator.Result.SsidTooLong>(tooLong)
  }

  @Test
  fun `Multi-byte chars in SSID`() {
    // len = 14 normally, len = 14 * 3 when encoded as byte array
    // Above the limit
    val fail = SsidValidator.validate("オール・ヘイル・ルルーシュ！")
    // len = 10 normally, len = 10 * 3 when encoded as byte array
    // Barely around the limit
    val pass = SsidValidator.validate("シュタインズ・ゲート")
    assertIs<SsidValidator.Result.SsidTooLong>(fail)
    assertIs<SsidValidator.Result.Success>(pass)
  }
}
