package dev.shadoe.hotspotapi.helper

import android.net.wifi.SoftApConfigurationHidden
import androidx.annotation.IntDef
import dev.shadoe.hotspotapi.R

object SoftApSpeedType {
    /** Wi-Fi hotspot band unknown. */
    const val BAND_UNKNOWN = 0

    const val BAND_2GHZ = SoftApConfigurationHidden.BAND_2GHZ

    const val BAND_5GHZ = SoftApConfigurationHidden.BAND_5GHZ

    const val BAND_6GHZ = SoftApConfigurationHidden.BAND_6GHZ

    const val BAND_60GHZ = SoftApConfigurationHidden.BAND_60GHZ

    const val BAND_ANY = SoftApConfigurationHidden.BAND_ANY

    @Retention(AnnotationRetention.SOURCE)
    @IntDef(value = [BAND_2GHZ, BAND_5GHZ, BAND_6GHZ, BAND_60GHZ])
    annotation class BandType

    fun getResOfSpeedType(
        @BandType speedType: Int,
    ) = when (speedType) {
        BAND_2GHZ -> R.string.freq_band_2_4_GHz
        BAND_5GHZ -> R.string.freq_band_5_GHz
        BAND_6GHZ -> R.string.freq_band_6_GHz
        BAND_60GHZ -> R.string.freq_band_60_GHz
        else -> R.string.freq_band_unknown
    }
}
