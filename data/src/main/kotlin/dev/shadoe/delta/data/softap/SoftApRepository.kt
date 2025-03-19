package dev.shadoe.delta.data.softap

import android.net.IIntResultListener
import android.net.ITetheringConnector
import android.net.MacAddress
import android.net.TetheringManager
import android.net.TetheringManager.TETHERING_WIFI
import android.net.wifi.IStringListener
import android.net.wifi.IWifiManager
import android.net.wifi.SoftApConfigurationHidden
import android.os.Binder
import android.os.Build
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import dev.rikka.tools.refine.Refine
import dev.shadoe.delta.api.SoftApCapabilities
import dev.shadoe.delta.api.SoftApConfiguration
import dev.shadoe.delta.api.SoftApEnabledState
import dev.shadoe.delta.api.SoftApEnabledState.EnabledStateType
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
  @TetheringSystemService private val tetheringConnector: ITetheringConnector,
  @WifiSystemService private val wifiManager: IWifiManager,
  @MacAddressCache private val persistedMacAddressCache: DataStore<Preferences>,
) {
  companion object {
    private const val TAG = "SoftApRepository"
    private const val ADB_PACKAGE_NAME = "com.android.shell"
  }

  private val internalState =
    MutableStateFlow(
      InternalState(fallbackPassphrase = generateRandomPassword())
    )

  private val shouldRestartHotspot = MutableStateFlow(false)

  private val _status =
    MutableStateFlow(
      SoftApStatus(
        enabledState = wifiManager.wifiApEnabledState,
        tetheredClients = emptyList(),
        capabilities =
          SoftApCapabilities(
            maxSupportedClients = 0,
            clientForceDisconnectSupported = false,
            isMacAddressCustomizationSupported = false,
            supportedFrequencyBands = emptyList(),
            supportedSecurityTypes = emptyList(),
          ),
      )
    )

  private val _config =
    MutableStateFlow(
      Refine.unsafeCast<SoftApConfigurationHidden>(
          wifiManager.softApConfiguration
        )
        .toBridgeClass(state = internalState.value)
    )

  private val tetheringEventListener =
    object : TetheringEventListener {
      override fun onEnabledStateChanged(@EnabledStateType state: Int) {
        _status.update { it.copy(enabledState = state) }
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

      override fun onSoftApCapabilitiesChanged(
        capabilities: SoftApCapabilities
      ) {
        _status.update { it.copy(capabilities = capabilities) }
      }
    }

  private val tetheringEventCallback =
    TetheringEventCallback(tetheringEventListener)

  private val softApCallback =
    SoftApCallback(tetheringEventListener, wifiManager)

  private val dummyIntResultReceiver =
    object : IIntResultListener.Stub() {
      override fun onResult(resultCode: Int) {}
    }

  private val updateConfigOnExternalChange =
    flow {
        var prev =
          Refine.unsafeCast<SoftApConfigurationHidden>(
            wifiManager.softApConfiguration
          )
        emit(prev)
        while (true) {
          runCatching {
              Refine.unsafeCast<SoftApConfigurationHidden>(
                wifiManager.softApConfiguration
              )
            }
            .getOrNull()
            ?.let { curr ->
              if (prev != curr) {
                emit(curr)
                prev = curr
              }
              delay(1.seconds)
            }
        }
      }
      .onEach { _config.value = it.toBridgeClass(state = internalState.value) }

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

  val config = _config.asStateFlow()
  val status = _status.asStateFlow()

  inner class CallbackSubscriber internal constructor(scope: CoroutineScope) :
    AutoCloseable {
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
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
        wifiManager.queryLastConfiguredTetheredApPassphraseSinceBoot(
          object : IStringListener.Stub() {
            override fun onResult(value: String?) {
              internalState.update {
                it.copy(fallbackPassphrase = value ?: generateRandomPassword())
              }
              _config.update {
                it.copy(passphrase = internalState.value.fallbackPassphrase)
              }
            }
          }
        )
      }
      scope.launch {
        withContext(Dispatchers.Unconfined) {
          updateConfigOnExternalChange.launchIn(this)
          restartHotspotOnConfigChange.launchIn(this)
          updateMacAddressCacheInMem.launchIn(this)
        }
      }
    }

    override fun close() {
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
  }

  fun callbackSubscriber(scope: CoroutineScope) = CallbackSubscriber(scope)

  fun startHotspot(forceRestart: Boolean = false): Boolean {
    val enabledState = status.value.enabledState
    var shouldStart = enabledState == SoftApEnabledState.WIFI_AP_STATE_DISABLED
    if (forceRestart) {
      shouldStart = enabledState == SoftApEnabledState.WIFI_AP_STATE_FAILED
    }
    if (!shouldStart) return false
    val request =
      TetheringManager.TetheringRequest.Builder(TETHERING_WIFI).build()
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
      tetheringConnector.startTethering(
        request.parcel,
        ADB_PACKAGE_NAME,
        null,
        dummyIntResultReceiver,
      )
    } else {
      @Suppress("DEPRECATION")
      tetheringConnector.startTethering(
        request.parcel,
        ADB_PACKAGE_NAME,
        dummyIntResultReceiver,
      )
    }
    return true
  }

  fun stopHotspot(): Boolean {
    val state = status.value.enabledState
    if (state != SoftApEnabledState.WIFI_AP_STATE_ENABLED) {
      return false
    }
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
      tetheringConnector.stopTethering(
        TETHERING_WIFI,
        ADB_PACKAGE_NAME,
        null,
        dummyIntResultReceiver,
      )
    } else {
      @Suppress("DEPRECATION")
      tetheringConnector.stopTethering(
        TETHERING_WIFI,
        ADB_PACKAGE_NAME,
        dummyIntResultReceiver,
      )
    }
    return true
  }

  fun updateSoftApConfiguration(c: SoftApConfiguration): Boolean =
    runCatching {
        Refine.unsafeCast<android.net.wifi.SoftApConfiguration>(
            c.toOriginalClass()
          )
          .let {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
              if (!wifiManager.validateSoftApConfiguration(it)) {
                return@let false
              }
            }
            wifiManager.setSoftApConfiguration(it, ADB_PACKAGE_NAME)
            return@let true
          }
      }
      .onFailure { Log.e(TAG, it.stackTraceToString()) }
      .getOrDefault(false)
      .also {
        if (it) {
          _config.update { c }
          shouldRestartHotspot.value = true
        }
      }
}
