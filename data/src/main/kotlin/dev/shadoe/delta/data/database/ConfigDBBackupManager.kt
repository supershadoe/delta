package dev.shadoe.delta.data.database

import android.content.Context
import android.net.Uri
import dagger.hilt.android.qualifiers.ApplicationContext
import dev.shadoe.delta.data.modules.DaoModule
import dev.shadoe.delta.data.qualifiers.ConfigDatabase
import java.io.File
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream
import java.util.zip.ZipOutputStream
import javax.inject.Inject
import kotlin.io.path.Path
import kotlin.io.path.div
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ConfigDBBackupManager
@Inject
constructor(
  @ApplicationContext private val applicationContext: Context,
  @ConfigDatabase private val configDb: ConfigDB,
) {
  companion object {
    const val DB_NAME = DaoModule.DB_NAME
  }

  private fun getDbFiles(): List<File> {
    val currentDbFile = applicationContext.getDatabasePath(DB_NAME)
    return listOf(
      currentDbFile,
      File(currentDbFile.parentFile, "$DB_NAME-wal"),
      File(currentDbFile.parentFile, "$DB_NAME-shm"),
    )
  }

  suspend fun importDatabase(file: Uri) =
    withContext(Dispatchers.IO) {
      configDb.close()
      val dbFiles = getDbFiles()

      dbFiles.forEach { it.takeIf { it.exists() }?.delete() }

      val inputStream = applicationContext.contentResolver.openInputStream(file)
      inputStream ?: throw FileNotFoundException("$file cannot be opened.")

      val zipInputStream = ZipInputStream(inputStream)
      val dbPath = dbFiles[0].parentFile!!
      var entry = zipInputStream.nextEntry
      while (entry != null) {
        val zipFilePath = Path(dbPath.path) / entry.name
        if (zipFilePath.toRealPath().parent != dbPath) {
          throw IllegalArgumentException("Potentially malicious zip file")
        }
        FileOutputStream(zipFilePath.toFile()).use { zipInputStream.copyTo(it) }
        entry = zipInputStream.nextEntry
      }

      zipInputStream.close()
      inputStream.close()
    }

  suspend fun exportDatabase(destination: Uri) =
    withContext(Dispatchers.IO) {
      configDb.query("PRAGMA wal_checkpoint(FULL)", emptyArray())

      val outputStream =
        applicationContext.contentResolver.openOutputStream(destination)
      outputStream
        ?: throw FileNotFoundException("$destination cannot be opened.")

      val zipOutputStream = ZipOutputStream(outputStream)
      getDbFiles()
        .filter { it.exists() }
        .forEach { file ->
          zipOutputStream.putNextEntry(ZipEntry(file.name))
          FileInputStream(file).use { it.copyTo(zipOutputStream) }
          zipOutputStream.closeEntry()
        }

      zipOutputStream.close()
      outputStream.close()
    }
}
