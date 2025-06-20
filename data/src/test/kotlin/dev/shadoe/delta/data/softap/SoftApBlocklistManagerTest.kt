package dev.shadoe.delta.data.softap

import dev.shadoe.delta.api.ACLDevice
import dev.shadoe.delta.api.MacAddress
import dev.shadoe.delta.api.SoftApConfiguration
import dev.shadoe.delta.data.MacAddressCacheRepository
import io.mockk.coEvery
import io.mockk.mockk
import io.mockk.slot
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.test.runTest

class SoftApBlocklistManagerTest {
  private lateinit var macAddressCacheRepository: MacAddressCacheRepository
  private lateinit var softApController: SoftApController
  private lateinit var softApStateStore: SoftApStateStore
  private lateinit var blocklistManager: SoftApBlocklistManager

  @BeforeTest
  fun setUp() {
    macAddressCacheRepository = mockk()
    softApController = mockk()
    softApStateStore = SoftApStateStore()
    blocklistManager =
      SoftApBlocklistManager(
        macAddressCacheRepository,
        softApController,
        softApStateStore,
      )

    coEvery { macAddressCacheRepository.getHostnamesFromCache(any()) } returns
      mapOf(
        MacAddress("AA:BB:CC:DD:EE:FF") to "host1",
        MacAddress("66:77:88:99:00:AA") to "host2",
      )

    val configSlot = slot<SoftApConfiguration>()
    coEvery {
      softApController.updateSoftApConfiguration(capture(configSlot))
    } answers
      {
        softApStateStore.mConfig.update { configSlot.captured }
        true
      }
  }

  @Test
  fun `blockedClients fetches hostnames from cache`() = runTest {
    softApStateStore.mConfig.update { c ->
      c.copy(
        blockedDevices =
          listOf(
            MacAddress("00:11:22:33:44:55"),
            MacAddress("AA:BB:CC:DD:EE:FF"),
          )
      )
    }
    val expected =
      listOf(
        ACLDevice(
          hostname = null,
          macAddress = MacAddress("00:11:22:33:44:55"),
        ),
        ACLDevice(
          hostname = "host1",
          macAddress = MacAddress("AA:BB:CC:DD:EE:FF"),
        ),
      )
    val result = blocklistManager.blockedClients.first()
    assertEquals(expected, result)
  }

  @Test
  fun `blockDevices adds new devices to the blocklist`() = runTest {
    softApStateStore.mConfig.update { c ->
      c.copy(blockedDevices = listOf(MacAddress("00:11:22:33:44:55")))
    }
    val initialExpected =
      listOf(
        ACLDevice(hostname = null, macAddress = MacAddress("00:11:22:33:44:55"))
      )
    val initialResult = blocklistManager.blockedClients.first()
    assertEquals(initialExpected, initialResult)

    blocklistManager.blockDevices(
      listOf(
        ACLDevice(
          hostname = "host1",
          macAddress = MacAddress("AA:BB:CC:DD:EE:FF"),
        ),
        ACLDevice(
          hostname = "host2",
          macAddress = MacAddress("66:77:88:99:00:AA"),
        ),
      )
    )
    val expected =
      listOf(
        ACLDevice(
          hostname = null,
          macAddress = MacAddress("00:11:22:33:44:55"),
        ),
        ACLDevice(
          hostname = "host1",
          macAddress = MacAddress("AA:BB:CC:DD:EE:FF"),
        ),
        ACLDevice(
          hostname = "host2",
          macAddress = MacAddress("66:77:88:99:00:AA"),
        ),
      )
    val result = blocklistManager.blockedClients.first()
    assertEquals(expected, result)
  }

  @Test
  fun `unblockDevices removes specified devices from the blocklist`() =
    runTest {
      softApStateStore.mConfig.update { c ->
        c.copy(
          blockedDevices =
            listOf(
              MacAddress("00:11:22:33:44:55"),
              MacAddress("AA:BB:CC:DD:EE:FF"),
              MacAddress("66:77:88:99:00:AA"),
            )
        )
      }
      val initialExpected =
        listOf(
          ACLDevice(
            hostname = null,
            macAddress = MacAddress("00:11:22:33:44:55"),
          ),
          ACLDevice(
            hostname = "host1",
            macAddress = MacAddress("AA:BB:CC:DD:EE:FF"),
          ),
          ACLDevice(
            hostname = "host2",
            macAddress = MacAddress("66:77:88:99:00:AA"),
          ),
        )
      val initialResult = blocklistManager.blockedClients.first()
      assertEquals(initialExpected, initialResult)

      blocklistManager.unblockDevices(
        listOf(
          ACLDevice(
            hostname = null,
            macAddress = MacAddress("00:11:22:33:44:55"),
          ),
          ACLDevice(
            hostname = "host2",
            macAddress = MacAddress("66:77:88:99:00:AA"),
          ),
        )
      )
      val expected =
        listOf(
          ACLDevice(
            hostname = "host1",
            macAddress = MacAddress("AA:BB:CC:DD:EE:FF"),
          )
        )
      val result = blocklistManager.blockedClients.first()
      assertEquals(expected, result)
    }
}
