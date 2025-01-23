package dev.shadoe.hotspotapi

import androidx.annotation.IntDef
import kotlin.intArrayOf

internal object WifiApEnabledStates {
    /**
     * Wi-Fi AP is currently being disabled. The state will change to
     * {@link #WIFI_AP_STATE_DISABLED} if it finishes successfully.
     */
    const val WIFI_AP_STATE_DISABLING = 10
    /**
     * Wi-Fi AP is disabled.
     */
    const val WIFI_AP_STATE_DISABLED = 11;
    /**
     * Wi-Fi AP is currently being enabled. The state will change to
     * {@link #WIFI_AP_STATE_ENABLED} if it finishes successfully.
     */
    const val WIFI_AP_STATE_ENABLING = 12
    /**
     * Wi-Fi AP is enabled.
     */
    const val WIFI_AP_STATE_ENABLED = 1
    /**
     * Wi-Fi AP is in a failed state. This state will occur when an error occurs during
     * enabling or disabling
     */
    const val WIFI_AP_STATE_FAILED = 14

    @IntDef(value = [WIFI_AP_STATE_DISABLING, WIFI_AP_STATE_DISABLED, WIFI_AP_STATE_ENABLING, WIFI_AP_STATE_ENABLED, WIFI_AP_STATE_FAILED])
    @Retention(AnnotationRetention.SOURCE)
    annotation class WifiApEnabledState
}