package dev.shadoe.delta.api

import androidx.annotation.IntDef

object SoftApRandomizationSetting {
  const val RANDOMIZATION_NONE = 0
  const val RANDOMIZATION_PERSISTENT = 1
  const val RANDOMIZATION_NON_PERSISTENT = 2

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
