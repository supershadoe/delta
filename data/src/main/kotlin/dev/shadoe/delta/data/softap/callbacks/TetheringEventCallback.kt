package dev.shadoe.delta.data.softap.callbacks

import android.net.ITetheringEventCallback
import android.net.Network
import android.net.TetherStatesParcel
import android.net.TetheredClient
import android.net.TetheringCallbackStartedParcel
import android.net.TetheringConfigurationParcel
import android.net.TetheringManagerHidden
import android.os.Build
import dev.shadoe.delta.api.LinkAddress
import dev.shadoe.delta.api.TetheredClient as TetheredClientWrapper
import dev.shadoe.delta.data.softap.internal.Extensions.toBridgeClass
import dev.shadoe.delta.data.softap.internal.TetheringEventListener
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

internal class TetheringEventCallback(
  private val tetheringEventListener: TetheringEventListener
) : ITetheringEventCallback.Stub() {
  private var tetheringSupported = false

  override fun onCallbackStarted(parcel: TetheringCallbackStartedParcel?) {
    parcel ?: return
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
      tetheringSupported = parcel.supportedTypes != 0L
    }
    tetheringSupported =
      tetheringSupported or parcel.config.tetherableWifiRegexs.isNotEmpty()
    tetheringEventListener.onSoftApSupported(isSupported = tetheringSupported)
    onTetherClientsChanged(parcel.tetheredClients)
  }

  override fun onCallbackStopped(errorCode: Int) {}

  override fun onUpstreamChanged(network: Network?) {}

  override fun onConfigurationChanged(config: TetheringConfigurationParcel?) {
    config ?: return
    tetheringSupported =
      tetheringSupported or config.tetherableWifiRegexs.isNotEmpty()
    tetheringEventListener.onSoftApSupported(isSupported = tetheringSupported)
  }

  override fun onTetherStatesChanged(states: TetherStatesParcel?) {}

  override fun onTetherClientsChanged(clients: List<TetheredClient?>?) {
    runBlocking {
      launch {
        processTetheredClients(clients).let {
          tetheringEventListener.onTetheredClientsChanged(it)
        }
      }
    }
  }

  override fun onOffloadStatusChanged(status: Int) {}

  override fun onSupportedTetheringTypes(supportedBitmap: Long) {}

  private suspend fun processTetheredClients(clients: List<TetheredClient?>?) =
    withContext(Dispatchers.Unconfined) {
      (clients ?: emptyList())
        .filterNotNull()
        .filter { it.tetheringType == TetheringManagerHidden.TETHERING_WIFI }
        .map { c ->
          val addresses = c.addresses.filterNotNull()
          val address = addresses.firstOrNull()?.address
          val hostname = addresses.firstNotNullOfOrNull { it.hostname }

          TetheredClientWrapper(
            macAddress = c.macAddress.toBridgeClass(),
            address = address?.address?.let { LinkAddress(it) },
            hostname = hostname,
            tetheringType = c.tetheringType,
          )
        }
    }
}
