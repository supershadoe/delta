package dev.shadoe.hotspotapi.callbacks

import android.net.ITetheringEventCallback
import android.net.Network
import android.net.TetherStatesParcel
import android.net.TetheredClient
import android.net.TetheringCallbackStartedParcel
import android.net.TetheringConfigurationParcel
import android.net.TetheringManager
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
                HotspotState.instance!!.enabledState.value = getHotspotState()
            }
        }
    }

    override fun onTetherClientsChanged(clients: List<TetheredClient?>?) {
        runBlocking {
            launch(Dispatchers.Unconfined) {
                HotspotState.instance!!.tetheredClients.value =
                    clients?.filterNotNull()
                        ?.filter { it.tetheringType == TetheringManager.TETHERING_WIFI }
                        ?: emptyList()
            }
        }
    }

    override fun onOffloadStatusChanged(status: Int) {}

    override fun onSupportedTetheringTypes(supportedBitmap: Long) {}
}