package dev.shadoe.hotspotapi.helper

import android.net.wifi.SoftApConfigurationHidden
import androidx.annotation.IntDef
import dev.shadoe.hotspotapi.R

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
    @IntDef(
        value = [
            SECURITY_TYPE_OPEN,
            SECURITY_TYPE_WPA2_PSK,
            SECURITY_TYPE_WPA3_SAE_TRANSITION,
            SECURITY_TYPE_WPA3_SAE,
            SECURITY_TYPE_WPA3_OWE_TRANSITION,
            SECURITY_TYPE_WPA3_OWE,
        ],
    )
    annotation class SecurityType

    fun getResOfSecurityType(
        @SecurityType securityType: Int,
    ) = when (securityType) {
        SECURITY_TYPE_OPEN -> R.string.security_proto_open
        SECURITY_TYPE_WPA2_PSK -> R.string.security_proto_wpa2_psk
        SECURITY_TYPE_WPA3_SAE -> R.string.security_proto_wpa3_sae
        SECURITY_TYPE_WPA3_SAE_TRANSITION ->
            R.string.security_proto_wpa3_sae_transition
        else -> R.string.security_proto_not_supported
    }

    val supportedSecurityTypes =
        listOf(
            SECURITY_TYPE_WPA3_SAE,
            SECURITY_TYPE_WPA3_SAE_TRANSITION,
            SECURITY_TYPE_WPA2_PSK,
            SECURITY_TYPE_OPEN,
        )
}
