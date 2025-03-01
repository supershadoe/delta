package dev.shadoe.delta.domain

import dev.shadoe.delta.data.softap.SoftApRepository
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Ignore
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock

@Ignore("Need to stub MacAddress or abstract it")
@RunWith(MockitoJUnitRunner::class)
class GetHotspotStatusTest {
  @Test
  fun `Retrieve status`() {
    val softApRepository =
      mock<SoftApRepository> {
        on { status } doReturn MutableStateFlow(Stubs.softApStatus)
      }
    val getHotspotStatus = GetHotspotStatus(softApRepository)
    val result = getHotspotStatus().value
    assertEquals(Stubs.softApStatus, result)
  }
}
