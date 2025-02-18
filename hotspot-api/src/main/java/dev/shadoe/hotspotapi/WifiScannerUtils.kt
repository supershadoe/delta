package dev.shadoe.hotspotapi

/**
 * Constants and util methods from android.net.wifi.WifiScanner
 */
object WifiScannerUtils {
    /** 5 GHz band excluding DFS channels */
    const val WIFI_BAND_5_GHZ = 1 shl 1
    /** DFS channels from 5 GHz band only */
    const val WIFI_BAND_5_GHZ_DFS_ONLY  = 1 shl 2
    /** 6 GHz band */
    const val WIFI_BAND_6_GHZ = 1 shl 3
    /** 5 GHz band channels with and without DFS */
    const val WIFI_BAND_5_GHZ_WITH_DFS = WIFI_BAND_5_GHZ or WIFI_BAND_5_GHZ_DFS_ONLY

    /**
     * Wifi SoftAp (Mobile Hotspot) operational mode.
     */
    const val OP_MODE_SAP = 1 shl 1

    /**
     * Filter channel based on regulatory constraints.
     */
    const val FILTER_REGULATORY = 0
}