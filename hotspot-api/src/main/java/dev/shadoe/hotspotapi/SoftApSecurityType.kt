package dev.shadoe.hotspotapi

import android.net.wifi.SoftApConfigurationHidden
import androidx.annotation.IntDef

object SoftApSecurityType {
    const val SECURITY_TYPE_OPEN = SoftApConfigurationHidden.SECURITY_TYPE_OPEN

    const val SECURITY_TYPE_WPA2_PSK =
        SoftApConfigurationHidden.SECURITY_TYPE_WPA2_PSK

    const val SECURITY_TYPE_WPA3_SAE_TRANSITION =
        SoftApConfigurationHidden.SECURITY_TYPE_WPA3_SAE_TRANSITION

    const val SECURITY_TYPE_WPA3_SAE =
        SoftApConfigurationHidden.SECURITY_TYPE_WPA3_SAE

    const val SECURITY_TYPE_WPA3_OWE_TRANSITION =
        SoftApConfigurationHidden.SECURITY_TYPE_WPA3_OWE_TRANSITION

    const val SECURITY_TYPE_WPA3_OWE =
        SoftApConfigurationHidden.SECURITY_TYPE_WPA3_OWE

    @Retention(AnnotationRetention.SOURCE)
    @IntDef(value = [SECURITY_TYPE_OPEN, SECURITY_TYPE_WPA2_PSK, SECURITY_TYPE_WPA3_SAE_TRANSITION, SECURITY_TYPE_WPA3_SAE, SECURITY_TYPE_WPA3_OWE_TRANSITION, SECURITY_TYPE_WPA3_OWE])
    annotation class SecurityType
}