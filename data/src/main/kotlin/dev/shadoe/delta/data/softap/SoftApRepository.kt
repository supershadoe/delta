package dev.shadoe.delta.data.softap

import android.content.Context
import android.net.IIntResultListener
import android.net.ITetheringConnector
import android.net.MacAddress
import android.net.TetheringManager
import android.net.TetheringManager.TETHERING_WIFI
import android.net.wifi.IStringListener
import android.net.wifi.IWifiManager
import android.net.wifi.SoftApConfigurationHidden
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import dagger.hilt.android.qualifiers.ApplicationContext
import dev.rikka.tools.refine.Refine
import dev.shadoe.delta.api.SoftApConfiguration
import dev.shadoe.delta.api.SoftApEnabledState
import dev.shadoe.delta.api.SoftApStatus
import dev.shadoe.delta.api.TetheredClient
import dev.shadoe.delta.data.services.TetheringSystemService
import dev.shadoe.delta.data.services.WifiSystemService
import dev.shadoe.delta.data.softap.callbacks.SoftApCallback
import dev.shadoe.delta.data.softap.callbacks.TetheringEventCallback
import dev.shadoe.delta.data.softap.internal.Extensions.toBridgeClass
import dev.shadoe.delta.data.softap.internal.Extensions.toOriginalClass
import dev.shadoe.delta.data.softap.internal.InternalState
import dev.shadoe.delta.data.softap.internal.TetheringEventListener
import dev.shadoe.delta.data.softap.internal.Utils.generateRandomPassword
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

private typealias PrefMapT = Map.Entry<Preferences.Key<String>, String>

@Singleton
class SoftApRepository
@Inject
constructor(
  @ApplicationContext private val applicationContext: Context,
  @TetheringSystemService private val tetheringConnector: ITetheringConnector,
  @WifiSystemService private val wifiManager: IWifiManager,
  @MacAddressCache private val persistedMacAddressCache: DataStore<Preferences>,
) {
  companion object {
    private const val ADB_PACKAGE_NAME = "com.android.shell"
  }

  private val internalState = MutableStateFlow(InternalState())

  private val shouldRestartHotspot = MutableStateFlow(false)

  private val _status =
    MutableStateFlow(
      SoftApStatus(
        enabledState = wifiManager.wifiApEnabledState,
        tetheredClients = emptyList(),
        supportedSpeedTypes = emptyList(),
        maxSupportedClients = 0,
      )
    )
  val status = _status.asStateFlow()

  val config =
    MutableStateFlow(
      Refine.unsafeCast<SoftApConfigurationHidden>(
          wifiManager.softApConfiguration
        )
        .toBridgeClass(state = internalState.value)
    )

  private val tetheringEventListener =
    object : TetheringEventListener {
      override fun onEnabledStateChanged() {
        _status.update {
          it.copy(enabledState = wifiManager.wifiApEnabledState)
        }
      }

      override fun onTetheredClientsChanged(clients: List<TetheredClient>) {
        _status.update { it.copy(tetheredClients = clients) }
        runBlocking {
          launch {
            persistedMacAddressCache.edit { prefs ->
              clients
                .filter { it.hostname != null }
                .map {
                  stringPreferencesKey(name = it.macAddress.toString()) to
                    it.hostname!!
                }
                .let { prefs.putAll(*it.toTypedArray()) }
            }
          }
        }
      }

      override fun onSupportedFrequencyBandsChanged(frequencyBands: List<Int>) {
        _status.update { it.copy(supportedSpeedTypes = frequencyBands) }
      }

      override fun onMaxClientLimitChanged(maxClients: Int) {
        _status.update { it.copy(maxSupportedClients = maxClients) }
      }
    }

  private val tetheringEventCallback =
    TetheringEventCallback(tetheringEventListener)

  private val softApCallback = SoftApCallback(tetheringEventListener)

  private val startOrStopResultReceiver =
    object : IIntResultListener.Stub() {
      override fun onResult(resultCode: Int) {
        tetheringEventListener.onEnabledStateChanged()
      }
    }

  private val updateConfigOnExternalChange =
    flow {
        var prev =
          Refine.unsafeCast<SoftApConfigurationHidden>(
            wifiManager.softApConfiguration
          )
        emit(prev)
        while (true) {
          val curr =
            Refine.unsafeCast<SoftApConfigurationHidden>(
              wifiManager.softApConfiguration
            )
          if (prev != curr) {
            emit(curr)
            prev = curr
          }
          delay(1.seconds)
        }
      }
      .onEach { config.value = it.toBridgeClass(state = internalState.value) }

  private val restartHotspotOnConfigChange =
    shouldRestartHotspot.onEach {
      if (!it) return@onEach

      val enabled = SoftApEnabledState.WIFI_AP_STATE_ENABLED
      val disabled = SoftApEnabledState.WIFI_AP_STATE_DISABLED
      if (status.value.enabledState == enabled) {
        stopHotspot()
        while (status.value.enabledState != disabled) {
          delay(500.milliseconds)
        }
        startHotspot()
        while (status.value.enabledState != enabled) {
          delay(500.milliseconds)
        }
      }

      shouldRestartHotspot.value = false
    }

  private val updateMacAddressCacheInMem =
    persistedMacAddressCache.data
      .map {
        it.asMap().asIterable().filterIsInstance<PrefMapT>().associate {
          MacAddress.fromString(it.key.name) to it.value
        }
      }
      .onEach { internalState.update { st -> st.copy(macAddressCache = it) } }

  fun startBackgroundJobs(scope: CoroutineScope) {
    updateConfigOnExternalChange.launchIn(scope)
    restartHotspotOnConfigChange.launchIn(scope)
    updateMacAddressCacheInMem.launchIn(scope)
  }

  fun onCreate(scope: CoroutineScope) {
    tetheringConnector.registerTetheringEventCallback(
      tetheringEventCallback,
      ADB_PACKAGE_NAME,
    )
    wifiManager.registerSoftApCallback(softApCallback)
    wifiManager.queryLastConfiguredTetheredApPassphraseSinceBoot(
      object : IStringListener.Stub() {
        override fun onResult(value: String?) {
          internalState.update {
            it.copy(fallbackPassphrase = value ?: generateRandomPassword())
          }
          config.update {
            it.copy(passphrase = internalState.value.fallbackPassphrase)
          }
        }
      }
    )
    scope.launch {
      withContext(Dispatchers.Unconfined) { startBackgroundJobs(this) }
    }
  }

  fun onDestroy() {
    tetheringConnector.unregisterTetheringEventCallback(
      tetheringEventCallback,
      ADB_PACKAGE_NAME,
    )
    wifiManager.unregisterSoftApCallback(softApCallback)
  }

  fun startHotspot() {
    val request =
      TetheringManager.TetheringRequest.Builder(TETHERING_WIFI).build()
    tetheringConnector.startTethering(
      request.parcel,
      applicationContext.packageName,
      applicationContext.attributionTag,
      startOrStopResultReceiver,
    )
  }

  fun stopHotspot() {
    tetheringConnector.stopTethering(
      TETHERING_WIFI,
      applicationContext.packageName,
      applicationContext.attributionTag,
      startOrStopResultReceiver,
    )
  }

  fun updateSoftApConfiguration(c: SoftApConfiguration): Boolean =
    runCatching {
        Refine.unsafeCast<android.net.wifi.SoftApConfiguration>(
            c.toOriginalClass()
          )
          .let {
            if (!wifiManager.validateSoftApConfiguration(it)) {
              return@let false
            }
            wifiManager.setSoftApConfiguration(it, ADB_PACKAGE_NAME)
            return@let true
          }
      }
      .getOrDefault(false)
      .also { shouldRestartHotspot.value = true }
}
