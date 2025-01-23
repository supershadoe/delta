package dev.shadoe.delta.shizuku

import android.content.pm.PackageManager
import rikka.shizuku.Shizuku
import rikka.shizuku.ShizukuProvider

object ShizukuUtils {
    internal fun checkShizukuPerm() =
        (!Shizuku.isPreV11()) && (Shizuku.checkSelfPermission() == PackageManager.PERMISSION_GRANTED)

    internal fun isShizukuInstalled(packageManager: PackageManager) =
        runCatching {
            packageManager.getApplicationInfo(
                ShizukuProvider.MANAGER_APPLICATION_ID, 0
            )
        }.getOrNull().let { it != null }

    internal const val PERM_REQ_CODE = 2345

    fun getPerm() = Shizuku.requestPermission(PERM_REQ_CODE)
}