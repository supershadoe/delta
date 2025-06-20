package dev.shadoe.delta.data

import dev.shadoe.delta.api.MacAddress
import dev.shadoe.delta.api.TetheredClient
import dev.shadoe.delta.data.database.dao.HostInfoDao
import dev.shadoe.delta.data.database.models.HostInfo
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlinx.coroutines.test.runTest

class MacAddressCacheRepositoryTest {
  private lateinit var hostInfoDao: HostInfoDao
  private lateinit var repository: MacAddressCacheRepository

  @BeforeTest
  fun setup() {
    hostInfoDao = mockk()
    repository = MacAddressCacheRepository(hostInfoDao)
  }

  @Test
  fun `updateHostInfoInCache filters out clients with null hostnames`() =
    runTest {
      val clients =
        listOf(
          TetheredClient(
            macAddress = MacAddress("00:11:22:33:44:55"),
            address = null,
            hostname = "host1",
            tetheringType = 0,
          ),
          TetheredClient(
            macAddress = MacAddress("AA:BB:CC:DD:EE:FF"),
            address = null,
            hostname = null,
            tetheringType = 0,
          ),
          TetheredClient(
            macAddress = MacAddress("66:77:88:99:00:AA"),
            address = null,
            hostname = "host3",
            tetheringType = 0,
          ),
        )
      val expectedHostInfo =
        arrayOf(
          HostInfo(
            macAddress = MacAddress("00:11:22:33:44:55"),
            hostname = "host1",
          ),
          HostInfo(
            macAddress = MacAddress("66:77:88:99:00:AA"),
            hostname = "host3",
          ),
        )
      coEvery { hostInfoDao.addHostInfo(*anyVararg()) } returns Unit
      repository.updateHostInfoInCache(clients)
      coVerify(exactly = 1) { hostInfoDao.addHostInfo(*expectedHostInfo) }
    }

  @Test
  fun `updateHostInfoInCache handles empty client list`() = runTest {
    coEvery { hostInfoDao.addHostInfo(*anyVararg()) } returns Unit
    repository.updateHostInfoInCache(emptyList())
    coVerify(exactly = 1) { hostInfoDao.addHostInfo() }
  }

  @Test
  fun `getHostnamesFromCache correctly maps MACs to hostnames`() = runTest {
    val macAddressList =
      listOf(
        MacAddress("00:11:22:33:44:55"),
        MacAddress("AA:BB:CC:DD:EE:FF"),
        MacAddress("66:77:88:99:00:AA"),
      )
    val hostInfoList =
      listOf(
        HostInfo(
          macAddress = MacAddress("00:11:22:33:44:55"),
          hostname = "host1",
        ),
        HostInfo(
          macAddress = MacAddress("66:77:88:99:00:AA"),
          hostname = "host3",
        ),
      )
    val expectedMap =
      mapOf(
        MacAddress("00:11:22:33:44:55") to "host1",
        MacAddress("66:77:88:99:00:AA") to "host3",
      )
    coEvery {
      hostInfoDao.resolveMacAddressesToHostNames(macAddressList)
    } returns hostInfoList
    val result = repository.getHostnamesFromCache(macAddressList)
    assertEquals(expectedMap, result)
  }

  @Test
  fun `getHostnamesFromCache handles empty mac address list`() = runTest {
    coEvery { hostInfoDao.resolveMacAddressesToHostNames(emptyList()) } returns
      emptyList()
    val result = repository.getHostnamesFromCache(emptyList())
    assertTrue { result.isEmpty() }
  }
}
