package dev.shadoe.delta.data.softap

import android.net.ITetheringConnector
import android.net.wifi.IWifiManager
import dev.shadoe.delta.data.qualifiers.TetheringSystemService
import dev.shadoe.delta.data.qualifiers.WifiSystemService
import javax.inject.Inject

class SoftApStateRepository
@Inject
constructor(
  @TetheringSystemService private val tetheringConnector: ITetheringConnector,
  @WifiSystemService private val wifiManager: IWifiManager,
) {}
