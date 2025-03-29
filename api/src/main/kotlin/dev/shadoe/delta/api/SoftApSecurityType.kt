package dev.shadoe.delta.api

import androidx.annotation.IntDef

object SoftApSecurityType {
  const val SECURITY_TYPE_OPEN = 0
  const val SECURITY_TYPE_WPA2_PSK = 1
  const val SECURITY_TYPE_WPA3_SAE_TRANSITION = 2
  const val SECURITY_TYPE_WPA3_SAE = 3
  const val SECURITY_TYPE_WPA3_OWE_TRANSITION = 4
  const val SECURITY_TYPE_WPA3_OWE = 5

  @Retention(AnnotationRetention.SOURCE)
  @IntDef(
    value =
      [
        SECURITY_TYPE_OPEN,
        SECURITY_TYPE_WPA2_PSK,
        SECURITY_TYPE_WPA3_SAE_TRANSITION,
        SECURITY_TYPE_WPA3_SAE,
        SECURITY_TYPE_WPA3_OWE_TRANSITION,
        SECURITY_TYPE_WPA3_OWE,
      ]
  )
  annotation class SecurityType
}
