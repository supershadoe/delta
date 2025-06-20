package dev.shadoe.delta.data.softap

import android.net.ITetheringConnector
import android.net.wifi.IWifiManager
import android.os.Build
import dev.shadoe.delta.api.SoftApAutoShutdownTimeout
import dev.shadoe.delta.api.SoftApConfiguration
import dev.shadoe.delta.api.SoftApEnabledState
import dev.shadoe.delta.api.SoftApRandomizationSetting
import dev.shadoe.delta.api.SoftApSecurityType
import dev.shadoe.delta.api.SoftApSpeedType
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
// BAKLAVA not yet supported by Robolectric
@Config(sdk = [Build.VERSION_CODES.VANILLA_ICE_CREAM])
class SoftApControllerTest {
  companion object {
    private val softApConfig =
      SoftApConfiguration(
        ssid = "test",
        passphrase = "password",
        securityType = SoftApSecurityType.SECURITY_TYPE_WPA2_PSK,
        macRandomizationSetting = SoftApRandomizationSetting.RANDOMIZATION_NONE,
        isHidden = false,
        speedType = SoftApSpeedType.BAND_2GHZ,
        blockedDevices = emptyList(),
        allowedClients = emptyList(),
        isAutoShutdownEnabled = false,
        autoShutdownTimeout = SoftApAutoShutdownTimeout.DEFAULT,
        maxClientLimit = 16,
      )
  }

  private lateinit var tetheringConnector: ITetheringConnector
  private lateinit var wifiManager: IWifiManager
  private lateinit var softApStateStore: SoftApStateStore

  @BeforeTest
  fun setup() {
    tetheringConnector = mockk(relaxed = true)
    wifiManager = mockk(relaxed = true)
    softApStateStore = SoftApStateStore()
  }

  @Test
  @Config(
    sdk =
      [
        Build.VERSION_CODES.VANILLA_ICE_CREAM,
        Build.VERSION_CODES.S,
        Build.VERSION_CODES.R,
      ]
  )
  fun `startSoftAp starts tethering when AP is DOWN`() = runTest {
    softApStateStore.mStatus.update { s ->
      s.copy(enabledState = SoftApEnabledState.WIFI_AP_STATE_DISABLED)
    }
    val softApController =
      SoftApController(tetheringConnector, wifiManager, softApStateStore, this)
    val result = softApController.startSoftAp()
    assertTrue(result)
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
      verify(atMost = 1) {
        tetheringConnector.startTethering(any(), any(), any(), any())
      }
    } else {
      verify(atMost = 1) {
        @Suppress("DEPRECATION")
        tetheringConnector.startTethering(any(), any(), any())
      }
    }
  }

  @Test
  fun `startSoftAp does not start tethering when AP is not DOWN`() = runTest {
    softApStateStore.mStatus.update { s ->
      s.copy(enabledState = SoftApEnabledState.WIFI_AP_STATE_ENABLED)
    }
    val softApController =
      SoftApController(tetheringConnector, wifiManager, softApStateStore, this)
    assertFalse(softApController.startSoftAp())
    softApStateStore.mStatus.update { s ->
      s.copy(enabledState = SoftApEnabledState.WIFI_AP_STATE_ENABLING)
    }
    assertFalse(softApController.startSoftAp())
    softApStateStore.mStatus.update { s ->
      s.copy(enabledState = SoftApEnabledState.WIFI_AP_STATE_DISABLING)
    }
    assertFalse(softApController.startSoftAp())
    softApStateStore.mStatus.update { s ->
      s.copy(enabledState = SoftApEnabledState.WIFI_AP_STATE_FAILED)
    }
    assertFalse(softApController.startSoftAp())
  }

  @Test
  @Config(
    sdk =
      [
        Build.VERSION_CODES.VANILLA_ICE_CREAM,
        Build.VERSION_CODES.S,
        Build.VERSION_CODES.R,
      ]
  )
  fun `stopSoftAp starts tethering when AP is UP`() = runTest {
    softApStateStore.mStatus.update { s ->
      s.copy(enabledState = SoftApEnabledState.WIFI_AP_STATE_ENABLED)
    }
    val softApController =
      SoftApController(tetheringConnector, wifiManager, softApStateStore, this)
    val result = softApController.stopSoftAp()
    assertTrue(result)
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
      verify(atMost = 1) {
        tetheringConnector.stopTethering(any(), any(), any(), any())
      }
    } else {
      verify(atMost = 1) {
        @Suppress("DEPRECATION")
        tetheringConnector.stopTethering(any(), any(), any())
      }
    }
  }

  @Test
  fun `stopSoftAp does not start tethering when AP is not UP`() = runTest {
    softApStateStore.mStatus.update { s ->
      s.copy(enabledState = SoftApEnabledState.WIFI_AP_STATE_DISABLED)
    }
    val softApController =
      SoftApController(tetheringConnector, wifiManager, softApStateStore, this)
    assertFalse(softApController.stopSoftAp())
    softApStateStore.mStatus.update { s ->
      s.copy(enabledState = SoftApEnabledState.WIFI_AP_STATE_ENABLING)
    }
    assertFalse(softApController.stopSoftAp())
    softApStateStore.mStatus.update { s ->
      s.copy(enabledState = SoftApEnabledState.WIFI_AP_STATE_DISABLING)
    }
    assertFalse(softApController.stopSoftAp())
    softApStateStore.mStatus.update { s ->
      s.copy(enabledState = SoftApEnabledState.WIFI_AP_STATE_FAILED)
    }
    assertFalse(softApController.stopSoftAp())
  }

  @Test
  @Config(
    sdk =
      [
        Build.VERSION_CODES.VANILLA_ICE_CREAM,
        Build.VERSION_CODES.UPSIDE_DOWN_CAKE,
        Build.VERSION_CODES.TIRAMISU,
      ]
  )
  fun `updateSoftApConfiguration updates system soft AP configuration`() =
    runTest {
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
        every { wifiManager.validateSoftApConfiguration(any()) } returns true
      }
      val softApController =
        SoftApController(
          tetheringConnector,
          wifiManager,
          softApStateStore,
          this.backgroundScope,
        )
      assertTrue(softApController.updateSoftApConfiguration(softApConfig))
      assertEquals(softApConfig, softApStateStore.config.value)
      verify(exactly = 1) { wifiManager.setSoftApConfiguration(any(), any()) }
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
        verify(exactly = 1) { wifiManager.validateSoftApConfiguration(any()) }
      }
    }

  @Test
  @Config(
    sdk =
      [
        Build.VERSION_CODES.VANILLA_ICE_CREAM,
        Build.VERSION_CODES.UPSIDE_DOWN_CAKE,
      ]
  )
  fun `updateSoftApConfiguration does not accept invalid config on Android 14+`() =
    runTest {
      every { wifiManager.validateSoftApConfiguration(any()) } returns false
      val softApController =
        SoftApController(
          tetheringConnector,
          wifiManager,
          softApStateStore,
          this.backgroundScope,
        )
      val currentConfig = softApStateStore.config.value
      assertFalse(softApController.updateSoftApConfiguration(softApConfig))
      assertEquals(currentConfig, softApStateStore.config.value)
      verify(exactly = 0) { wifiManager.setSoftApConfiguration(any(), any()) }
      verify(exactly = 1) { wifiManager.validateSoftApConfiguration(any()) }
    }

  @OptIn(ExperimentalCoroutinesApi::class)
  @Test
  fun `updateSoftApConfiguration restarts soft AP if AP is already UP`() =
    runTest {
      every { wifiManager.validateSoftApConfiguration(any()) } returns true
      every { tetheringConnector.stopTethering(any(), any(), any(), any()) }
        .answers {
          softApStateStore.mStatus.update { s ->
            s.copy(enabledState = SoftApEnabledState.WIFI_AP_STATE_DISABLED)
          }
        }
      every { tetheringConnector.startTethering(any(), any(), any(), any()) }
        .answers {
          softApStateStore.mStatus.update { s ->
            s.copy(enabledState = SoftApEnabledState.WIFI_AP_STATE_ENABLED)
          }
        }

      softApStateStore.mStatus.update { s ->
        s.copy(enabledState = SoftApEnabledState.WIFI_AP_STATE_ENABLED)
      }
      val softApController =
        SoftApController(
          tetheringConnector,
          wifiManager,
          softApStateStore,
          this,
        )
      val result = softApController.updateSoftApConfiguration(softApConfig)
      advanceUntilIdle()

      assertTrue(result)
      assertEquals(softApConfig, softApStateStore.config.value)
      verify(exactly = 1) { wifiManager.setSoftApConfiguration(any(), any()) }
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
        verify(exactly = 1) { wifiManager.validateSoftApConfiguration(any()) }
      }

      verify(exactly = 1) {
        tetheringConnector.stopTethering(any(), any(), any(), any())
      }
      verify(exactly = 1) {
        tetheringConnector.startTethering(any(), any(), any(), any())
      }
    }
}
