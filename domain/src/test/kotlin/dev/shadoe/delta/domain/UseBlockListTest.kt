package dev.shadoe.delta.domain

import dev.shadoe.delta.api.ACLDevice
import dev.shadoe.delta.api.MacAddress
import dev.shadoe.delta.data.softap.SoftApRepository
import kotlin.test.Ignore
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock

@RunWith(MockitoJUnitRunner::class)
class UseBlockListTest {
  @Test
  fun `Get block list`() = runTest {
    val configStub = Stubs.getSoftApConfiguration()
    val softApRepository =
      mock<SoftApRepository> {
        on { config } doReturn MutableStateFlow(configStub)
      }
    val getHotspotConfig = GetHotspotConfig(softApRepository)
    val editHotspotConfig = EditHotspotConfig(softApRepository)
    val useBlockList = UseBlockList(getHotspotConfig, editHotspotConfig)
    val result = useBlockList.getBlockedClientsFlow().first()
    assertEquals(configStub.blockedDevices, result)
  }

  @Ignore("Requires wifiManager stubbing")
  @Test
  fun `Add a device to block list`() {
    val configStub = Stubs.getSoftApConfiguration()
    val softApRepository =
      mock<SoftApRepository> {
        on { config } doReturn MutableStateFlow(configStub)
      }
    val getHotspotConfig = GetHotspotConfig(softApRepository)
    val editHotspotConfig = EditHotspotConfig(softApRepository)
    val newDevice =
      ACLDevice(hostname = null, macAddress = MacAddress("cc:cc:11:2f:a3:aa"))
    val useBlockList = UseBlockList(getHotspotConfig, editHotspotConfig)
    val result = useBlockList.blockClient(newDevice)
    assertEquals(result, true)
  }

  @Ignore("Requires wifiManager stubbing")
  @Test
  fun `Remove a device from block list`() {
    val configStub = Stubs.getSoftApConfiguration()
    val softApRepository =
      mock<SoftApRepository> {
        on { config } doReturn MutableStateFlow(configStub)
      }
    val getHotspotConfig = GetHotspotConfig(softApRepository)
    val editHotspotConfig = EditHotspotConfig(softApRepository)
    val deviceToRemove = configStub.blockedDevices.random()
    val useBlockList = UseBlockList(getHotspotConfig, editHotspotConfig)
    val result = useBlockList.unblockClient(deviceToRemove)
    assertEquals(result, true)
  }
}
