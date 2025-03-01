package dev.shadoe.delta.domain

import dev.shadoe.delta.api.SoftApEnabledState
import dev.shadoe.delta.api.SoftApStatus
import dev.shadoe.delta.data.softap.SoftApRepository
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock

@RunWith(MockitoJUnitRunner::class)
class ControlHotspotTest {
  @Test
  fun `Start hotspot when it is disabled`() {
    val softApRepository =
      mock<SoftApRepository> {
        on { status } doReturn
          MutableStateFlow(
            SoftApStatus(
              enabledState = SoftApEnabledState.WIFI_AP_STATE_DISABLED,
              tetheredClients = emptyList(),
              supportedSpeedTypes = emptyList(),
              maxSupportedClients = 0,
            )
          )
      }
    val controlHotspot = ControlHotspot(softApRepository)
    val result = controlHotspot.startHotspot(forceRestart = false)
    assertEquals(true, result)
  }

  @Test
  fun `Start hotspot when it is already enabled`() {
    val softApRepository =
      mock<SoftApRepository> {
        on { status } doReturn
          MutableStateFlow(
            SoftApStatus(
              enabledState = SoftApEnabledState.WIFI_AP_STATE_ENABLED,
              tetheredClients = emptyList(),
              supportedSpeedTypes = emptyList(),
              maxSupportedClients = 0,
            )
          )
      }
    val controlHotspot = ControlHotspot(softApRepository)
    val result = controlHotspot.startHotspot(forceRestart = false)
    assertEquals(false, result)
  }

  @Test
  fun `Force start hotspot`() {
    val softApRepository =
      mock<SoftApRepository> {
        on { status } doReturn
          MutableStateFlow(
            SoftApStatus(
              enabledState = SoftApEnabledState.WIFI_AP_STATE_FAILED,
              tetheredClients = emptyList(),
              supportedSpeedTypes = emptyList(),
              maxSupportedClients = 0,
            )
          )
      }
    val controlHotspot = ControlHotspot(softApRepository)
    val result = controlHotspot.startHotspot(forceRestart = true)
    assertEquals(true, result)
  }

  @Test
  fun `Stop hotspot when it is enabled`() {
    val softApRepository =
      mock<SoftApRepository> {
        on { status } doReturn
          MutableStateFlow(
            SoftApStatus(
              enabledState = SoftApEnabledState.WIFI_AP_STATE_ENABLED,
              tetheredClients = emptyList(),
              supportedSpeedTypes = emptyList(),
              maxSupportedClients = 0,
            )
          )
      }
    val controlHotspot = ControlHotspot(softApRepository)
    val result = controlHotspot.stopHotspot()
    assertEquals(true, result)
  }

  @Test
  fun `Stop hotspot when it is already disabled`() {
    val softApRepository =
      mock<SoftApRepository> {
        on { status } doReturn
          MutableStateFlow(
            SoftApStatus(
              enabledState = SoftApEnabledState.WIFI_AP_STATE_DISABLED,
              tetheredClients = emptyList(),
              supportedSpeedTypes = emptyList(),
              maxSupportedClients = 0,
            )
          )
      }
    val controlHotspot = ControlHotspot(softApRepository)
    val result = controlHotspot.stopHotspot()
    assertEquals(false, result)
  }
}
