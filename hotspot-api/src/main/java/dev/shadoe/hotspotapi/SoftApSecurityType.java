package dev.shadoe.hotspotapi;

import android.net.wifi.SoftApConfigurationHidden;

import androidx.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class SoftApSecurityType {
    public static final int SECURITY_TYPE_OPEN = SoftApConfigurationHidden.SECURITY_TYPE_OPEN;

    public static final int SECURITY_TYPE_WPA2_PSK = SoftApConfigurationHidden.SECURITY_TYPE_WPA2_PSK;

    public static final int SECURITY_TYPE_WPA3_SAE_TRANSITION = SoftApConfigurationHidden.SECURITY_TYPE_WPA3_SAE_TRANSITION;

    public static final int SECURITY_TYPE_WPA3_SAE = SoftApConfigurationHidden.SECURITY_TYPE_WPA3_SAE;

    public static final int SECURITY_TYPE_WPA3_OWE_TRANSITION = SoftApConfigurationHidden.SECURITY_TYPE_WPA3_OWE_TRANSITION;

    public static final int SECURITY_TYPE_WPA3_OWE = SoftApConfigurationHidden.SECURITY_TYPE_WPA3_OWE;

    @Retention(RetentionPolicy.SOURCE)
    @IntDef(value = {SECURITY_TYPE_OPEN, SECURITY_TYPE_WPA2_PSK, SECURITY_TYPE_WPA3_SAE_TRANSITION, SECURITY_TYPE_WPA3_SAE, SECURITY_TYPE_WPA3_OWE_TRANSITION, SECURITY_TYPE_WPA3_OWE,})
    public @interface SecurityType {
    }
}