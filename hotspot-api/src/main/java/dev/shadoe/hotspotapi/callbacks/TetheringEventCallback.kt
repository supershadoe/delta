package dev.shadoe.hotspotapi.callbacks

import android.net.ITetheringEventCallback
import android.net.Network
import android.net.TetherStatesParcel
import android.net.TetheredClient
import android.net.TetheringCallbackStartedParcel
import android.net.TetheringConfigurationParcel

internal class TetheringEventCallback: ITetheringEventCallback.Stub() {
    override fun onCallbackStarted(parcel: TetheringCallbackStartedParcel?) {
//        TODO("Not yet implemented")
    }

    override fun onCallbackStopped(errorCode: Int) {
//        TODO("Not yet implemented")
    }

    override fun onUpstreamChanged(network: Network?) {
//        TODO("Not yet implemented")
    }

    override fun onConfigurationChanged(config: TetheringConfigurationParcel?) {
//        TODO("Not yet implemented")
    }

    override fun onTetherStatesChanged(states: TetherStatesParcel?) {
//        TODO("Not yet implemented")
    }

    override fun onTetherClientsChanged(clients: List<TetheredClient?>?) {
//        TODO("Not yet implemented")
        println(clients?.map {client -> client?.addresses?.map { address -> address.hostname }.toString()}?.toString())
    }

    override fun onOffloadStatusChanged(status: Int) {
//        TODO("Not yet implemented")
    }

    override fun onSupportedTetheringTypes(supportedBitmap: Long) {
//        TODO("Not yet implemented")
    }
}