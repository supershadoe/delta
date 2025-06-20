package dev.shadoe.delta.data

import dev.shadoe.delta.api.ConfigFlag
import dev.shadoe.delta.data.database.dao.FlagsDao
import dev.shadoe.delta.data.database.models.Flag
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import kotlinx.coroutines.test.runTest

class FlagsRepositoryTest {
  private lateinit var flagsDao: FlagsDao
  private lateinit var flagsRepository: FlagsRepository

  @BeforeTest
  fun setup() {
    flagsDao = mockk()
    flagsRepository = FlagsRepository(flagsDao)
  }

  @Test
  fun `isFirstRun when NOT_FIRST_RUN flag is not set or false`() = runTest {
    coEvery { flagsDao.getFlag(ConfigFlag.NOT_FIRST_RUN.ordinal) }
      .returns(null)
      .andThen(false)
    assertTrue { flagsRepository.isFirstRun() }
    assertTrue { flagsRepository.isFirstRun() }
  }

  @Test
  fun `isFirstRun when NOT_FIRST_RUN is true`() = runTest {
    coEvery { flagsDao.getFlag(ConfigFlag.NOT_FIRST_RUN.ordinal) } returns true
    assertFalse { flagsRepository.isFirstRun() }
  }

  @Test
  fun `isInsecureReceiverEnabled when the flag is not set or false`() =
    runTest {
      coEvery {
        flagsDao.getFlag(ConfigFlag.INSECURE_RECEIVER_ENABLED.ordinal)
      } returns null andThen false
      assertFalse { flagsRepository.isInsecureReceiverEnabled() }
      assertFalse { flagsRepository.isInsecureReceiverEnabled() }
    }

  @Test
  fun `isInsecureReceiverEnabled when the flag is true`() = runTest {
    coEvery {
      flagsDao.getFlag(ConfigFlag.INSECURE_RECEIVER_ENABLED.ordinal)
    } returns true
    assertTrue { flagsRepository.isInsecureReceiverEnabled() }
  }

  @Test
  fun `not first run flag gets set`() = runTest {
    coEvery { flagsDao.setFlag(any()) } returns Unit
    flagsRepository.setNotFirstRun()
    coVerify(exactly = 1) {
      flagsDao.setFlag(
        match {
          it == Flag(flag = ConfigFlag.NOT_FIRST_RUN.ordinal, value = true)
        }
      )
    }
  }

  @Test
  fun `insecure receiver flag gets set`() = runTest {
    coEvery { flagsDao.setFlag(any()) } returns Unit
    flagsRepository.setInsecureReceiverStatus(true)
    coVerify(exactly = 1) {
      flagsDao.setFlag(
        match {
          val flag =
            Flag(
              flag = ConfigFlag.INSECURE_RECEIVER_ENABLED.ordinal,
              value = true,
            )
          it == flag
        }
      )
    }
    flagsRepository.setInsecureReceiverStatus(false)
    coVerify(exactly = 1) {
      flagsDao.setFlag(
        match {
          val flag =
            Flag(
              flag = ConfigFlag.INSECURE_RECEIVER_ENABLED.ordinal,
              value = false,
            )
          it == flag
        }
      )
    }
  }
}
