package dev.shadoe.delta.api

import androidx.annotation.IntDef

object SoftApSpeedType {
  /** Wi-Fi hotspot band unknown. */
  const val BAND_UNKNOWN = 0

  const val BAND_2GHZ = 1 shl 0
  const val BAND_5GHZ = 1 shl 1
  const val BAND_6GHZ = 1 shl 2
  const val BAND_60GHZ = 1 shl 3

  @Retention(AnnotationRetention.SOURCE)
  @IntDef(value = [BAND_2GHZ, BAND_5GHZ, BAND_6GHZ, BAND_60GHZ])
  annotation class BandType
}
