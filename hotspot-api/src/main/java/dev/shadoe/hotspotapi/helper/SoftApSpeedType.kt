package dev.shadoe.hotspotapi.helper

import android.net.wifi.SoftApConfigurationHidden
import androidx.annotation.IntDef

object SoftApSpeedType {
    /** Wi-Fi hotspot band unknown. */
    const val BAND_UNKNOWN = 0

    const val BAND_2GHZ = SoftApConfigurationHidden.BAND_2GHZ

    const val BAND_5GHZ = SoftApConfigurationHidden.BAND_5GHZ

    const val BAND_6GHZ = SoftApConfigurationHidden.BAND_6GHZ

    const val BAND_60GHZ = SoftApConfigurationHidden.BAND_60GHZ

    const val BAND_ANY = SoftApConfigurationHidden.BAND_ANY

    @Retention(AnnotationRetention.SOURCE)
    @IntDef(flag = true, value = [BAND_2GHZ, BAND_5GHZ, BAND_6GHZ, BAND_60GHZ])
    annotation class BandType

    fun getNameOfSpeedType(
        @BandType speedType: Int,
    ): String =
        when (speedType) {
            BAND_2GHZ -> "2.4 GHz"
            BAND_5GHZ -> "5 GHz"
            BAND_6GHZ -> "6 GHz"
            BAND_60GHZ -> "60 GHz"
            else -> "Not supported"
        }
}
