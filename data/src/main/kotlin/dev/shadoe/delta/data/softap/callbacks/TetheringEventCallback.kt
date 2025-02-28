package dev.shadoe.delta.data.softap.callbacks

import android.net.ITetheringEventCallback
import android.net.Network
import android.net.TetherStatesParcel
import android.net.TetheredClient
import android.net.TetheringCallbackStartedParcel
import android.net.TetheringConfigurationParcel
import android.net.TetheringManager
import dev.shadoe.delta.data.softap.internal.TetheringEventListener
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

internal class TetheringEventCallback(
    private val tetheringEventListener: TetheringEventListener,
) : ITetheringEventCallback.Stub() {
    override fun onCallbackStarted(parcel: TetheringCallbackStartedParcel?) {
        parcel ?: return
        onTetherStatesChanged(parcel.states)
        onTetherClientsChanged(parcel.tetheredClients)
    }

    override fun onCallbackStopped(errorCode: Int) {}

    override fun onUpstreamChanged(network: Network?) {}

    override fun onConfigurationChanged(config: TetheringConfigurationParcel?) {
    }

    override fun onTetherStatesChanged(states: TetherStatesParcel?) {
        tetheringEventListener.onEnabledStateChanged()
    }

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
                .filter {
                    it.tetheringType ==
                        TetheringManager.TETHERING_WIFI
                }.map {
                    val addresses = it.addresses.filterNotNull()
                    val address = addresses.firstOrNull()?.address
                    val hostname = addresses.firstNotNullOfOrNull { it.hostname }

                    dev.shadoe.delta.api.TetheredClient(
                        macAddress = it.macAddress,
                        address = address,
                        hostname = hostname,
                        tetheringType = it.tetheringType,
                    )
                }
        }
}
