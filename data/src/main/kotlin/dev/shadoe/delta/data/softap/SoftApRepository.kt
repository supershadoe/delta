package dev.shadoe.delta.data.softap

import android.net.wifi.IStringListener
import android.net.wifi.IWifiManager
import android.net.wifi.SoftApConfigurationHidden
import android.os.Build
import android.util.Log
import dev.rikka.tools.refine.Refine
import dev.shadoe.delta.api.SoftApCapabilities
import dev.shadoe.delta.api.SoftApConfiguration
import dev.shadoe.delta.api.SoftApStatus
import dev.shadoe.delta.data.qualifiers.WifiSystemService
import dev.shadoe.delta.data.softap.internal.Extensions.toBridgeClass
import dev.shadoe.delta.data.softap.internal.Extensions.toOriginalClass
import dev.shadoe.delta.data.softap.internal.InternalState
import dev.shadoe.delta.data.softap.internal.Utils.generateRandomPassword
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.time.Duration.Companion.seconds
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Singleton
class SoftApRepository
@Inject
constructor(@WifiSystemService private val wifiManager: IWifiManager) {
  companion object {
    private const val TAG = "SoftApRepository"
    private const val ADB_PACKAGE_NAME = "com.android.shell"
  }

  private val internalState =
    MutableStateFlow(
      InternalState(fallbackPassphrase = generateRandomPassword())
    )

  private val shouldRestartHotspot = MutableStateFlow(false)

  internal val _status =
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

  internal val _config =
    MutableStateFlow(
      Refine.unsafeCast<SoftApConfigurationHidden>(
          wifiManager.softApConfiguration
        )
        .toBridgeClass(state = internalState.value)
    )

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

  val config = _config.asStateFlow()
  val status = _status.asStateFlow()

  inner class CallbackSubscriber internal constructor(scope: CoroutineScope) :
    AutoCloseable {
    init {

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
        }
      }
    }

    override fun close() {}
  }

  fun callbackSubscriber(scope: CoroutineScope) = CallbackSubscriber(scope)

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
