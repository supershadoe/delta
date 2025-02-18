package dev.shadoe.hotspotapi

import android.net.wifi.SoftApConfigurationHidden
import androidx.annotation.IntDef

object SoftApSpeedType {
    const val BAND_2GHZ = SoftApConfigurationHidden.BAND_2GHZ

    const val BAND_5GHZ = SoftApConfigurationHidden.BAND_5GHZ

    const val BAND_6GHZ = SoftApConfigurationHidden.BAND_6GHZ

    const val BAND_60GHZ = SoftApConfigurationHidden.BAND_60GHZ

    const val BAND_ANY = SoftApConfigurationHidden.BAND_ANY

    @Retention(AnnotationRetention.SOURCE)
    @IntDef(flag = true, value = [BAND_2GHZ, BAND_5GHZ, BAND_6GHZ, BAND_60GHZ,])
    annotation class BandType
}