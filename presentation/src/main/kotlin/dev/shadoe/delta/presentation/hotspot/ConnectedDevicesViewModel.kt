package dev.shadoe.delta.presentation.hotspot

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.shadoe.delta.domain.UseBlockList
import dev.shadoe.delta.domain.ViewConnectedClients
import dev.shadoe.hotspotapi.wrappers.ACLDevice
import javax.inject.Inject

@HiltViewModel
class ConnectedDevicesViewModel
    @Inject
    constructor(
        private val viewConnectedClients: ViewConnectedClients,
        private val useBlockList: UseBlockList,
    ) : ViewModel() {
        val connectedClients
            get() = viewConnectedClients()

        fun blockDevice(device: ACLDevice) = useBlockList.blockClient(device)
    }
