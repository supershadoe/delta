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
