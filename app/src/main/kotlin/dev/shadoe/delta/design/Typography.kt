package dev.shadoe.delta.design

import android.annotation.SuppressLint
import android.os.Build
import androidx.compose.material3.Typography
import dev.shadoe.delta.design.fonts.StaticFontFamily
import dev.shadoe.delta.design.fonts.VariableFontFamily

object Typography {
  private val defaultTypography = Typography()

  // Potentially will backport app < SDK 30
  // Marked as to do elsewhere in [VariableFontFamily]
  @SuppressLint("ObsoleteSdkInt")
  private val fontFamily =
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      VariableFontFamily
    } else {
      StaticFontFamily
    }

  private val displayLarge =
    defaultTypography.displayLarge.copy(fontFamily = fontFamily)
  private val displayMedium =
    defaultTypography.displayMedium.copy(fontFamily = fontFamily)
  private val displaySmall =
    defaultTypography.displaySmall.copy(fontFamily = fontFamily)
  private val headlineLarge =
    defaultTypography.headlineLarge.copy(fontFamily = fontFamily)
  private val headlineMedium =
    defaultTypography.headlineMedium.copy(fontFamily = fontFamily)
  private val headlineSmall =
    defaultTypography.headlineSmall.copy(fontFamily = fontFamily)
  private val titleLarge =
    defaultTypography.titleLarge.copy(fontFamily = fontFamily)
  private val titleMedium =
    defaultTypography.titleMedium.copy(fontFamily = fontFamily)
  private val titleSmall =
    defaultTypography.titleSmall.copy(fontFamily = fontFamily)
  private val bodyLarge =
    defaultTypography.bodyLarge.copy(fontFamily = fontFamily)
  private val bodyMedium =
    defaultTypography.bodyMedium.copy(fontFamily = fontFamily)
  private val bodySmall =
    defaultTypography.bodySmall.copy(fontFamily = fontFamily)
  private val labelLarge =
    defaultTypography.labelLarge.copy(fontFamily = fontFamily)
  private val labelMedium =
    defaultTypography.labelMedium.copy(fontFamily = fontFamily)
  private val labelSmall =
    defaultTypography.labelSmall.copy(fontFamily = fontFamily)

  val value =
    Typography(
      displayLarge = displayLarge,
      displayMedium = displayMedium,
      displaySmall = displaySmall,
      headlineLarge = headlineLarge,
      headlineMedium = headlineMedium,
      headlineSmall = headlineSmall,
      titleLarge = titleLarge,
      titleMedium = titleMedium,
      titleSmall = titleSmall,
      bodyLarge = bodyLarge,
      bodyMedium = bodyMedium,
      bodySmall = bodySmall,
      labelLarge = labelLarge,
      labelMedium = labelMedium,
      labelSmall = labelSmall,
    )
}
