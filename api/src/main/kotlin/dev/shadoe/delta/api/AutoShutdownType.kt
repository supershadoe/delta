package dev.shadoe.delta.api

import androidx.annotation.IntDef

object AutoShutdownType {
  const val FIVE_MINUTES = 5 * 60 * 1000
  const val TEN_MINUTES = 10 * 60 * 1000
  const val TWENTY_MINUTES = 20 * 60 * 1000
  const val THIRTY_MINUTES = 30 * 60 * 1000
  const val ONE_HOUR = 60 * 60 * 1000
  const val DEFAULT = -1

  @Retention(AnnotationRetention.SOURCE)
  @IntDef(
    value =
      [
        FIVE_MINUTES,
        TEN_MINUTES,
        TWENTY_MINUTES,
        THIRTY_MINUTES,
        ONE_HOUR,
        DEFAULT,
      ]
  )
  annotation class AvailableAutoShutdownType

  fun getResOfAutoShutdownType(
    @AvailableAutoShutdownType autoShutdownType: Int
  ) =
    when (autoShutdownType) {
      FIVE_MINUTES -> R.string.auto_shutdown_5
      TEN_MINUTES -> R.string.auto_shutdown_10
      TWENTY_MINUTES -> R.string.auto_shutdown_20
      THIRTY_MINUTES -> R.string.auto_shutdown_30
      ONE_HOUR -> R.string.auto_shutdown_60
      else -> {
        R.string.auto_shutdown_default
      }
    }
}
