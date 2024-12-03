package dev.shadoe.delta.shizuku

import android.content.Context
import android.net.wifi.IWifiManager
import android.net.wifi.SoftApConfiguration
import android.os.Build
import org.lsposed.hiddenapibypass.HiddenApiBypass
import rikka.shizuku.ShizukuBinderWrapper
import rikka.shizuku.SystemServiceHelper

object SystemApiAccess {
    init {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            HiddenApiBypass.setHiddenApiExemptions("Landroid/net/wifi/IWifiManager")
        }
    }

    val softApConfiguration: SoftApConfiguration
        get() {
            val wifiManager = IWifiManager.Stub.asInterface(
                ShizukuBinderWrapper(
                    SystemServiceHelper.getSystemService(
                        Context.WIFI_SERVICE
                    )
                )
            )
            return wifiManager.softApConfiguration
        }

    val ssid
        get() = when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.R -> softApConfiguration.ssid
            else -> null
        }

    val passphrase
        get() = when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.R -> softApConfiguration.passphrase
            else -> null
        }
}