package dev.shadoe.delta

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import org.lsposed.hiddenapibypass.HiddenApiBypass

@HiltAndroidApp
class Application : Application() {
    override fun onCreate() {
        super.onCreate()
        HiddenApiBypass.setHiddenApiExemptions("L")
    }
}
