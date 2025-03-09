package dev.shadoe.delta.api

import android.net.wifi.SoftApConfigurationHidden
import androidx.annotation.IntDef

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
}
