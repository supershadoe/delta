package dev.shadoe.delta.data

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import dev.shadoe.delta.api.ConfigFlag
import dev.shadoe.delta.data.database.ConfigDB
import dev.shadoe.delta.data.database.models.Flag
import dev.shadoe.delta.data.qualifiers.ConfigDatabase
import java.io.File
import javax.inject.Inject

class FlagsRepository
@Inject
constructor(
  @ConfigDatabase private val configDB: ConfigDB,
  @ApplicationContext private val applicationContext: Context,
) {
  private val flagsDao = configDB.flagsDao()

  suspend fun shouldMigrateToRoom() =
    flagsDao.getFlag(ConfigFlag.USES_ROOM_DB.ordinal) != true &&
      File(
          applicationContext.filesDir,
          "datastore/mac_address_cache.preferences_pb",
        )
        .exists()

  suspend fun isFirstRun() =
    flagsDao.getFlag(ConfigFlag.NOT_FIRST_RUN.ordinal) != true

  suspend fun setNotFirstRun() =
    flagsDao.setFlag(
      Flag(flag = ConfigFlag.NOT_FIRST_RUN.ordinal, value = true)
    )

  suspend fun debugDumpFlags() = flagsDao.dump()
}
