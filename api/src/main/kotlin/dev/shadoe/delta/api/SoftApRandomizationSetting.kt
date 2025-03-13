package dev.shadoe.delta.api

import android.net.wifi.SoftApConfigurationHidden
import androidx.annotation.IntDef
import dev.shadoe.delta.api.SoftApAutoShutdownTimeout.DEFAULT
import dev.shadoe.delta.api.SoftApAutoShutdownTimeout.FIVE_MINUTES
import dev.shadoe.delta.api.SoftApAutoShutdownTimeout.ONE_HOUR
import dev.shadoe.delta.api.SoftApAutoShutdownTimeout.TEN_MINUTES
import dev.shadoe.delta.api.SoftApAutoShutdownTimeout.THIRTY_MINUTES
import dev.shadoe.delta.api.SoftApAutoShutdownTimeout.TWENTY_MINUTES

object SoftApRandomizationSetting {
  const val RANDOMIZATION_NONE = SoftApConfigurationHidden.RANDOMIZATION_NONE
  const val RANDOMIZATION_PERSISTENT =
    SoftApConfigurationHidden.RANDOMIZATION_PERSISTENT
  const val RANDOMIZATION_NON_PERSISTENT =
    SoftApConfigurationHidden.RANDOMIZATION_NON_PERSISTENT

  @Retention(AnnotationRetention.SOURCE)
  @IntDef(
    value =
      [
        RANDOMIZATION_NONE,
        RANDOMIZATION_PERSISTENT,
        RANDOMIZATION_NON_PERSISTENT,
      ]
  )
  annotation class RandomizationType

  val supportedRandomizationSettings =
    listOf(
      RANDOMIZATION_NONE,
      RANDOMIZATION_PERSISTENT,
      RANDOMIZATION_NON_PERSISTENT,
    )
}
