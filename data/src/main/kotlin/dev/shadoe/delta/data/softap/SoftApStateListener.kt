package dev.shadoe.delta.data.softap

import android.net.ITetheringConnector
import android.net.wifi.IWifiManager
import android.os.Binder
import android.os.Build
import dev.shadoe.delta.api.SoftApCapabilities
import dev.shadoe.delta.api.SoftApEnabledState.EnabledStateType
import dev.shadoe.delta.api.TetheredClient
import dev.shadoe.delta.data.MacAddressCacheRepository
import dev.shadoe.delta.data.qualifiers.TetheringSystemService
import dev.shadoe.delta.data.qualifiers.WifiSystemService
import dev.shadoe.delta.data.softap.callbacks.SoftApCallback
import dev.shadoe.delta.data.softap.callbacks.TetheringEventCallback
import dev.shadoe.delta.data.softap.internal.TetheringEventListener
import dev.shadoe.delta.data.softap.internal.Utils.ADB_PACKAGE_NAME
import javax.inject.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SoftApStateListener
@Inject
constructor(
  @TetheringSystemService private val tetheringConnector: ITetheringConnector,
  @WifiSystemService private val wifiManager: IWifiManager,
  private val macAddressCacheRepository: MacAddressCacheRepository,
  private val softApStateRepository: SoftApStateRepository,
) : AutoCloseable, TetheringEventListener {
  private val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())

  private val tetheringEventCallback = TetheringEventCallback(this)

  private val softApCallback = SoftApCallback(this, wifiManager)

  init {
    tetheringConnector.registerTetheringEventCallback(
      tetheringEventCallback,
      ADB_PACKAGE_NAME,
    )
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
      wifiManager.registerSoftApCallback(softApCallback)
    } else {
      @Suppress("DEPRECATION")
      wifiManager.registerSoftApCallback(
        Binder(),
        softApCallback,
        softApCallback.hashCode(),
      )
    }
  }

  override fun close() {
    scope.cancel()
    tetheringConnector.unregisterTetheringEventCallback(
      tetheringEventCallback,
      ADB_PACKAGE_NAME,
    )
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
      wifiManager.unregisterSoftApCallback(softApCallback)
    } else {
      @Suppress("DEPRECATION")
      wifiManager.unregisterSoftApCallback(softApCallback.hashCode())
    }
  }

  override fun onEnabledStateChanged(@EnabledStateType state: Int) {
    softApStateRepository.status.update { it.copy(enabledState = state) }
  }

  override fun onTetheredClientsChanged(clients: List<TetheredClient>) {
    softApStateRepository.status.update { it.copy(tetheredClients = clients) }
    scope.launch {
      clients
        .filter { it.hostname != null }
        .map { it.macAddress.toString() to it.hostname!! }
        .let { macAddressCacheRepository.updateHostInfoInCache(it) }
    }
  }

  override fun onSoftApCapabilitiesChanged(capabilities: SoftApCapabilities) {
    softApStateRepository.status.update { it.copy(capabilities = capabilities) }
  }
}
