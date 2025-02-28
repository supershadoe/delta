package dev.shadoe.delta.presentation.hotspot

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.shadoe.delta.domain.UseBlockList
import dev.shadoe.hotspotapi.wrappers.ACLDevice
import javax.inject.Inject

@HiltViewModel
class BlockListViewModel
    @Inject
    constructor(
        private val useBlockList: UseBlockList,
    ) : ViewModel() {
        val blockedClients = useBlockList.getBlockedClientsFlow()

        fun unblockDevice(device: ACLDevice) = useBlockList.unblockClient(device)
    }
