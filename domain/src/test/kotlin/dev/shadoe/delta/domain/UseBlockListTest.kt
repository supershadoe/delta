package dev.shadoe.delta.domain

import dev.shadoe.delta.api.ACLDevice
import dev.shadoe.delta.api.MacAddress
import dev.shadoe.delta.data.softap.SoftApRepository
import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertEquals
import kotlin.test.assertFalse
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
    val useBlockList = UseBlockList(softApRepository)
    val result = useBlockList.getBlockedClientsFlow().first()
    assertEquals(configStub.blockedDevices, result)
  }

  @Test
  fun `Add a device to block list`() = runTest {
    val configStub = Stubs.getSoftApConfiguration()
    val softApRepository =
      mock<SoftApRepository> {
        on { config } doReturn MutableStateFlow(configStub)
      }
    val newDevice =
      ACLDevice(hostname = null, macAddress = MacAddress("cc:cc:11:2f:a3:aa"))
    val useBlockList = UseBlockList(softApRepository)
    useBlockList.blockClient(newDevice)
    val result = useBlockList.getBlockedClientsFlow().first()
    assertContains(result, newDevice)
  }

  @Test
  fun `Remove a device from block list`() = runTest {
    val configStub = Stubs.getSoftApConfiguration()
    val softApRepository =
      mock<SoftApRepository> {
        on { config } doReturn MutableStateFlow(configStub)
      }
    val deviceToRemove = configStub.blockedDevices.random()
    val useBlockList = UseBlockList(softApRepository)
    useBlockList.unblockClient(deviceToRemove)
    val result = useBlockList.getBlockedClientsFlow().first()
    assertFalse { result.contains(deviceToRemove) }
  }
}
