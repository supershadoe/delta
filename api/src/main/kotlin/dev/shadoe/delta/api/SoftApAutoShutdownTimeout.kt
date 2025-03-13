package dev.shadoe.delta.api

import android.net.wifi.SoftApConfigurationHidden
import androidx.annotation.LongDef

object SoftApAutoShutdownTimeout {
  const val DEFAULT = SoftApConfigurationHidden.DEFAULT_TIMEOUT
  const val FIVE_MINUTES = 5 * 60 * 1000L
  const val TEN_MINUTES = 10 * 60 * 1000L
  const val TWENTY_MINUTES = 20 * 60 * 1000L
  const val THIRTY_MINUTES = 30 * 60 * 1000L
  const val ONE_HOUR = 60 * 60 * 1000L

  @Retention(AnnotationRetention.SOURCE)
  @LongDef(
    value =
      [
        DEFAULT,
        FIVE_MINUTES,
        TEN_MINUTES,
        TWENTY_MINUTES,
        THIRTY_MINUTES,
        ONE_HOUR,
      ]
  )
  annotation class AutoShutdownTimeoutType

  fun getResOfTimeoutType(@AutoShutdownTimeoutType autoShutdownType: Long) =
    when (autoShutdownType) {
      DEFAULT -> R.string.auto_shutdown_default
      FIVE_MINUTES -> R.string.auto_shutdown_5
      TEN_MINUTES -> R.string.auto_shutdown_10
      TWENTY_MINUTES -> R.string.auto_shutdown_20
      THIRTY_MINUTES -> R.string.auto_shutdown_30
      ONE_HOUR -> R.string.auto_shutdown_60
      else -> R.string.auto_shutdown_default
    }

  val supportedShutdownTimeouts =
    listOf(
      DEFAULT,
      FIVE_MINUTES,
      TEN_MINUTES,
      TWENTY_MINUTES,
      THIRTY_MINUTES,
      ONE_HOUR,
    )
}
