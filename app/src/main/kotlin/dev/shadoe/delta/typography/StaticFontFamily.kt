package dev.shadoe.delta.typography

import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import dev.shadoe.delta.R

internal val StaticFontFamily =
  FontFamily(
    Font(R.font.nunitosans_extralight, weight = FontWeight.W200),
    Font(R.font.nunitosans_light, weight = FontWeight.W300),
    Font(R.font.nunitosans_regular, weight = FontWeight.W400),
    Font(R.font.nunitosans_medium, weight = FontWeight.W500),
    Font(R.font.nunitosans_semibold, weight = FontWeight.W600),
    Font(R.font.nunitosans_bold, weight = FontWeight.W700),
    Font(R.font.nunitosans_extrabold, weight = FontWeight.W800),
    Font(R.font.nunitosans_black, weight = FontWeight.W900),
    Font(
      R.font.nunitosans_extralightitalic,
      weight = FontWeight.W200,
      style = FontStyle.Italic,
    ),
    Font(
      R.font.nunitosans_lightitalic,
      weight = FontWeight.W300,
      style = FontStyle.Italic,
    ),
    Font(
      R.font.nunitosans_italic,
      weight = FontWeight.W400,
      style = FontStyle.Italic,
    ),
    Font(
      R.font.nunitosans_mediumitalic,
      weight = FontWeight.W500,
      style = FontStyle.Italic,
    ),
    Font(
      R.font.nunitosans_semibolditalic,
      weight = FontWeight.W600,
      style = FontStyle.Italic,
    ),
    Font(
      R.font.nunitosans_bolditalic,
      weight = FontWeight.W700,
      style = FontStyle.Italic,
    ),
    Font(
      R.font.nunitosans_extrabolditalic,
      weight = FontWeight.W800,
      style = FontStyle.Italic,
    ),
    Font(
      R.font.nunitosans_blackitalic,
      weight = FontWeight.W900,
      style = FontStyle.Italic,
    ),
  )
