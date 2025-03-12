package dev.shadoe.delta.api

import androidx.annotation.IntDef

object AutoShutdownType {
  const val FIVE_MINUTES = 300000
  const val TEN_MINUTES = 600000
  const val TWENTY_MINUTES = 1200000
  const val THIRTY_MINUTES = 1800000
  const val ONE_HOUR = 3600000
  const val NEVER = -1

  @Retention(AnnotationRetention.SOURCE)
  @IntDef(
    value =
      [
        FIVE_MINUTES,
        TEN_MINUTES,
        TWENTY_MINUTES,
        THIRTY_MINUTES,
        ONE_HOUR,
        NEVER,
      ]
  )
  annotation class AvailableAutoShutdownType

  fun getResOfAutoShutdownType(@AvailableAutoShutdownType autoShutdownType: Int) = when (autoShutdownType) {

    FIVE_MINUTES -> R.string.auto_shutdown_5
    TEN_MINUTES -> R.string.auto_shutdown_10
    TWENTY_MINUTES -> R.string.auto_shutdown_20
    THIRTY_MINUTES -> R.string.auto_shutdown_30
    ONE_HOUR -> R.string.auto_shutdown_60
    NEVER -> R.string.auto_shutdown_never
    else -> R.string.auto_shutdown_not_supported
  }  }
