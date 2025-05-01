package dev.shadoe.delta.data

import dev.shadoe.delta.api.ConfigFlag
import dev.shadoe.delta.data.database.dao.FlagsDao
import dev.shadoe.delta.data.database.models.Flag
import javax.inject.Inject

class FlagsRepository @Inject constructor(private val flagsDao: FlagsDao) {
  suspend fun isFirstRun() =
    flagsDao.getFlag(ConfigFlag.NOT_FIRST_RUN.ordinal) != true

  suspend fun setNotFirstRun() =
    flagsDao.setFlag(
      Flag(flag = ConfigFlag.NOT_FIRST_RUN.ordinal, value = true)
    )

  suspend fun isInsecureReceiverEnabled() =
    flagsDao.getFlag(ConfigFlag.INSECURE_RECEIVER_ENABLED.ordinal) == true

  suspend fun setInsecureReceiverStatus(enabled: Boolean) =
    flagsDao.setFlag(
      Flag(flag = ConfigFlag.INSECURE_RECEIVER_ENABLED.ordinal, value = enabled)
    )

  suspend fun debugDumpFlags() = flagsDao.dump()
}
