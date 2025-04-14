package dev.shadoe.delta.design

import androidx.compose.material3.Typography

object Typography {
  private val defaultTypography = Typography()

  private val displayLarge =
    defaultTypography.displayLarge.copy(fontFamily = VariableFontFamily)
  private val displayMedium =
    defaultTypography.displayMedium.copy(fontFamily = VariableFontFamily)
  private val displaySmall =
    defaultTypography.displaySmall.copy(fontFamily = VariableFontFamily)
  private val headlineLarge =
    defaultTypography.headlineLarge.copy(fontFamily = VariableFontFamily)
  private val headlineMedium =
    defaultTypography.headlineMedium.copy(fontFamily = VariableFontFamily)
  private val headlineSmall =
    defaultTypography.headlineSmall.copy(fontFamily = VariableFontFamily)
  private val titleLarge =
    defaultTypography.titleLarge.copy(fontFamily = VariableFontFamily)
  private val titleMedium =
    defaultTypography.titleMedium.copy(fontFamily = VariableFontFamily)
  private val titleSmall =
    defaultTypography.titleSmall.copy(fontFamily = VariableFontFamily)
  private val bodyLarge =
    defaultTypography.bodyLarge.copy(fontFamily = VariableFontFamily)
  private val bodyMedium =
    defaultTypography.bodyMedium.copy(fontFamily = VariableFontFamily)
  private val bodySmall =
    defaultTypography.bodySmall.copy(fontFamily = VariableFontFamily)
  private val labelLarge =
    defaultTypography.labelLarge.copy(fontFamily = VariableFontFamily)
  private val labelMedium =
    defaultTypography.labelMedium.copy(fontFamily = VariableFontFamily)
  private val labelSmall =
    defaultTypography.labelSmall.copy(fontFamily = VariableFontFamily)

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
