package dev.shadoe.delta.data

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import dev.shadoe.delta.api.ConfigFlag
import dev.shadoe.delta.data.database.dao.FlagsDao
import dev.shadoe.delta.data.database.models.Flag
import java.io.File
import javax.inject.Inject

class FlagsRepository
@Inject
constructor(
  @ApplicationContext private val applicationContext: Context,
  private val flagsDao: FlagsDao,
) {

  suspend fun shouldMigrateToRoom(): Boolean {
    var isRoomNotUsed =
      flagsDao.getFlag(ConfigFlag.USES_ROOM_DB.ordinal) != true
    File(
        applicationContext.filesDir,
        "datastore/mac_address_cache.preferences_pb",
      )
      .exists()
      .takeIf { !it && isRoomNotUsed }
      ?.run {
        flagsDao.setFlag(
          Flag(flag = ConfigFlag.USES_ROOM_DB.ordinal, value = true)
        )
        isRoomNotUsed = false
      }
    return isRoomNotUsed
  }

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
