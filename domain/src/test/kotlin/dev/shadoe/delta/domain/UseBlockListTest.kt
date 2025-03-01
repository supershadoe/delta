package dev.shadoe.delta.domain

import android.net.MacAddress
import dev.shadoe.delta.api.ACLDevice
import dev.shadoe.delta.data.softap.SoftApRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import kotlin.test.Test
import kotlinx.coroutines.test.runTest
import kotlin.test.Ignore
import kotlin.test.assertContains
import kotlin.test.assertEquals
import kotlin.test.assertFalse

@Ignore("Need to stub MacAddress or abstract it")
@RunWith(MockitoJUnitRunner::class)
class UseBlockListTest {
  @Test
  fun `Get block list`() = runTest {
    val softApRepository = mock<SoftApRepository> {
      on { config } doReturn MutableStateFlow(Stubs.softApConfiguration)
    }
    val useBlockList = UseBlockList(softApRepository)
    val result = useBlockList.getBlockedClientsFlow().first()
    assertEquals(Stubs.softApConfiguration.blockedDevices, result)
  }

  @Test
  fun `Add a device to block list`() = runTest {
    val macAddressMock = mock<MacAddress>()
    val softApRepository = mock<SoftApRepository> {
      on { config } doReturn MutableStateFlow(Stubs.softApConfiguration)
    }
    val hasUpdated = MutableStateFlow(false)
    val newDevice = ACLDevice(
      hostname = null,
      macAddress = MacAddress.fromString("cc:cc:11:2f:a3:aa"),
    )
    val useBlockList = UseBlockList(softApRepository)
    launch {
      val flow = useBlockList.getBlockedClientsFlow()
      flow.take(2).collect {
        if (hasUpdated.value) {
          assertContains(it, newDevice)
        } else {
          assertEquals(Stubs.softApConfiguration.blockedDevices, it)
        }
      }
    }
    useBlockList.blockClient(newDevice)
    hasUpdated.update { true }
  }

  @Test
  fun `Remove a device from block list`() = runTest {
    val softApRepository = mock<SoftApRepository> {
      on { config } doReturn MutableStateFlow(Stubs.softApConfiguration)
    }
    val hasUpdated = MutableStateFlow(false)
    val deviceToRemove = Stubs.softApConfiguration.blockedDevices.random()
    val useBlockList = UseBlockList(softApRepository)
    launch {
      val flow = useBlockList.getBlockedClientsFlow()
      flow.take(2).collect {
        if (hasUpdated.value) {
          assertFalse { it.contains(deviceToRemove) }
        } else {
          assertEquals(Stubs.softApConfiguration.blockedDevices, it)
        }
      }
    }
    useBlockList.unblockClient(deviceToRemove)
    hasUpdated.update { true }
  }
}
