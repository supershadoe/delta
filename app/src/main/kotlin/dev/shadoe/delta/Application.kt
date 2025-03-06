package dev.shadoe.delta

import android.app.Application
import android.util.Log
import dagger.hilt.android.HiltAndroidApp
import dev.shadoe.delta.crash.CrashHandler
import kotlin.system.exitProcess
import org.lsposed.hiddenapibypass.HiddenApiBypass

@HiltAndroidApp
class Application : Application() {
  override fun onCreate() {
    super.onCreate()

    Thread.setDefaultUncaughtExceptionHandler { _, throwable ->
      Log.e(packageName, "Uncaught exception", throwable)
      CrashHandler.sendCrashNotification(applicationContext, throwable)
      exitProcess(1)
    }

    HiddenApiBypass.setHiddenApiExemptions("L")
  }
}
