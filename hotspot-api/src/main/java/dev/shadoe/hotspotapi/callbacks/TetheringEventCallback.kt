package dev.shadoe.hotspotapi.callbacks

import android.net.ITetheringEventCallback
import android.net.Network
import android.net.TetherStatesParcel
import android.net.TetheredClient
import android.net.TetheringCallbackStartedParcel
import android.net.TetheringConfigurationParcel
import dev.shadoe.hotspotapi.HotspotState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

internal class TetheringEventCallback(private val getHotspotState: () -> Int) :
    ITetheringEventCallback.Stub() {

    override fun onCallbackStarted(parcel: TetheringCallbackStartedParcel?) {
        parcel ?: return
        onTetherStatesChanged(parcel.states)
        onTetherClientsChanged(parcel.tetheredClients)
    }

    override fun onCallbackStopped(errorCode: Int) {}

    override fun onUpstreamChanged(network: Network?) {}

    override fun onConfigurationChanged(config: TetheringConfigurationParcel?) {}

    override fun onTetherStatesChanged(states: TetherStatesParcel?) {
        runBlocking {
            launch(Dispatchers.Unconfined) {
                val state = getHotspotState()
                println(state)
                HotspotState.instance!!.enabledState.value = state
            }
        }
    }

    override fun onTetherClientsChanged(clients: List<TetheredClient?>?) {
        runBlocking {
            launch(Dispatchers.Unconfined) {
                HotspotState.instance!!.tetheredClients.value =
                    clients?.filterNotNull() ?: emptyList()
            }
        }
    }

    override fun onOffloadStatusChanged(status: Int) {}

    override fun onSupportedTetheringTypes(supportedBitmap: Long) {}
}