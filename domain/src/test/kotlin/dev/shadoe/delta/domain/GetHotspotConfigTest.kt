package dev.shadoe.delta.domain

import dev.shadoe.delta.data.softap.SoftApRepository
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock

@RunWith(MockitoJUnitRunner::class)
class GetHotspotConfigTest {
  @Test
  fun `Retrieve config`() {
    val configStub = Stubs.getSoftApConfiguration()
    val softApRepository =
      mock<SoftApRepository> {
        on { config } doReturn MutableStateFlow(configStub)
      }
    val getHotspotConfig = GetHotspotConfig(softApRepository)
    val result = getHotspotConfig().value
    assertEquals(configStub, result)
  }
}
