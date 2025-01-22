package dev.shadoe.delta.shizuku

import android.content.Context
import android.net.ITetheringConnector
import android.net.TetheringManager
import android.net.wifi.ISoftApCallback
import android.net.wifi.IWifiManager
import android.net.wifi.SoftApCapability
import android.net.wifi.SoftApInfo
import android.net.wifi.SoftApState
import android.net.wifi.WifiClient
import android.os.Binder
import android.os.Build
import android.os.IBinder
import dev.shadoe.delta.screens.ShizukuSetup
import org.lsposed.hiddenapibypass.HiddenApiBypass
import rikka.shizuku.ShizukuBinderWrapper
import rikka.shizuku.SystemServiceHelper

class SoftApCallback: Binder(), ISoftApCallback {
    override fun onStateChanged(state: SoftApState?) {
        println(state)
    }

    override fun onConnectedClientsOrInfoChanged(
        infos: Map<String?, SoftApInfo?>?,
        clients: Map<String?, List<WifiClient?>?>?,
        isBridged: Boolean,
        isRegistration: Boolean
    ) {
        println(clients)
    }

    override fun onCapabilityChanged(capability: SoftApCapability?) {
        println(capability)
    }

    override fun onBlockedClientConnecting(
        client: WifiClient?,
        blockedReason: Int
    ) {
        println(client)
    }

    override fun asBinder(): IBinder? = this
}

class StartTetheringCallback: TetheringManager.StartTetheringCallback {
    override fun onTetheringStarted() {
        println("Tethering started!!!!!!!!!!!!!!!!")
    }

    override fun onTetheringFailed(error: Int) {
        println("Tethering failed :(:(:(:(")
    }
}

object HotspotApi {
    init {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            HiddenApiBypass.setHiddenApiExemptions("L")
        }
    }

    val wifiManager: IWifiManager?
        get() = SystemServiceHelper.getSystemService(
            Context.WIFI_SERVICE
        )?.let {
            IWifiManager.Stub.asInterface(ShizukuBinderWrapper(it))
        }

    val tetheringManager: ITetheringConnector?
        get() = SystemServiceHelper.getSystemService("tethering")?.let {
            ITetheringConnector.Stub.asInterface(ShizukuBinderWrapper(it))
        }

    val softApConfiguration
        get() = runCatching { wifiManager?.softApConfiguration }.getOrNull()

    val ssid
        get() = when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.R -> softApConfiguration?.ssid
            else -> null
        }

    val passphrase
        get() = when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.R -> softApConfiguration?.passphrase
            else -> null
        }

    val softApCallback = SoftApCallback()
    val startTetheringCallback = StartTetheringCallback()
}