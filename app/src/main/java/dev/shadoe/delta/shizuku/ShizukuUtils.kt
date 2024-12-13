package dev.shadoe.delta.shizuku

import android.app.Application
import android.content.pm.PackageManager
import androidx.lifecycle.AndroidViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import rikka.shizuku.Shizuku
import rikka.shizuku.ShizukuProvider
import rikka.sui.Sui

object ShizukuUtils {
    internal fun checkShizukuPerm() =
        (!Shizuku.isPreV11()) && (Shizuku.checkSelfPermission() == PackageManager.PERMISSION_GRANTED)

    internal fun isShizukuInstalled(packageManager: PackageManager) =
        runCatching {
            packageManager.getApplicationInfo(
                ShizukuProvider.MANAGER_APPLICATION_ID, 0
            )
        }.getOrNull().let { it != null }

    private const val PERM_REQ_CODE = 2345

    fun getPerm() = Shizuku.requestPermission(PERM_REQ_CODE)

    class ShizukuViewModel(application: Application) :
        AndroidViewModel(application) {
        private val _isConnected = MutableStateFlow(false)
        val isConnected: StateFlow<Boolean> = _isConnected
        private val _isRunning = MutableStateFlow(false)
        val isRunning: StateFlow<Boolean> = _isRunning
        private val _isSuiAvailable = MutableStateFlow(false)
        val isSuiAvailable: StateFlow<Boolean> = _isSuiAvailable

        private val permListener =
            Shizuku.OnRequestPermissionResultListener { requestCode, grantResult ->
                if (requestCode == PERM_REQ_CODE) {
                    _isConnected.value =
                        grantResult == PackageManager.PERMISSION_GRANTED
                }
            }

        private val binderReceivedListener = Shizuku.OnBinderReceivedListener {
            _isRunning.value = true
            Shizuku.addRequestPermissionResultListener(permListener)
            _isConnected.value = checkShizukuPerm()
        }

        private val binderDeadListener = Shizuku.OnBinderDeadListener {
            _isRunning.value = false
            Shizuku.removeRequestPermissionResultListener(permListener)
        }

        init {
            Sui.init(application.packageName)
            _isRunning.value = Shizuku.pingBinder()
            if (_isRunning.value == true) {
                _isConnected.value = checkShizukuPerm()
            }
            _isSuiAvailable.value = Sui.isSui()
            Shizuku.addBinderReceivedListenerSticky(binderReceivedListener)
            Shizuku.addBinderDeadListener(binderDeadListener)
        }

        override fun onCleared() {
            Shizuku.removeBinderReceivedListener(binderReceivedListener)
            Shizuku.removeBinderDeadListener(binderDeadListener)
            super.onCleared()
        }
    }
}