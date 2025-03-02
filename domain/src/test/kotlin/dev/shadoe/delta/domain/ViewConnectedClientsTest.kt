package dev.shadoe.delta.domain

import dev.shadoe.delta.data.softap.SoftApRepository
import kotlin.test.assertEquals
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock

@RunWith(MockitoJUnitRunner::class)
class ViewConnectedClientsTest {
  @Test
  fun `Get connected devices`() = runTest {
    val statusStub = Stubs.getSoftApStatus()
    val softApRepository =
      mock<SoftApRepository> {
        on { status } doReturn MutableStateFlow(statusStub)
      }
    val viewConnectedClients = ViewConnectedClients(softApRepository)
    val result = viewConnectedClients().first()
    assertEquals(statusStub.tetheredClients, result)
  }
}
