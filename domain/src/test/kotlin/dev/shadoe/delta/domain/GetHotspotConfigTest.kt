package dev.shadoe.delta.domain

import dev.shadoe.delta.data.softap.SoftApRepository
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Ignore
import org.junit.runner.RunWith
import org.mockito.kotlin.mock
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.doReturn
import kotlin.test.Test
import kotlin.test.assertEquals

@Ignore("Need to stub MacAddress or abstract it")
@RunWith(MockitoJUnitRunner::class)
class GetHotspotConfigTest {
  @Test
  fun `Retrieve config`() {
    val softApRepository = mock<SoftApRepository> {
      on { config } doReturn MutableStateFlow(Stubs.softApConfiguration)
    }
    val getHotspotConfig = GetHotspotConfig(softApRepository)
    val result = getHotspotConfig().value
    assertEquals(Stubs.softApConfiguration, result)
  }
}
