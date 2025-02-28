package dev.shadoe.delta.presentation.shizuku

import android.app.Application
import android.content.pm.PackageManager
import androidx.lifecycle.AndroidViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import rikka.shizuku.Shizuku
import rikka.shizuku.ShizukuProvider
import rikka.sui.Sui

class ShizukuViewModel(
    private val application: Application,
) : AndroidViewModel(application) {
    @ShizukuStates.ShizukuStateType
    private val _shizukuState = MutableStateFlow(ShizukuStates.NOT_READY)

    @ShizukuStates.ShizukuStateType
    val shizukuState: StateFlow<Int> = _shizukuState

    private val permListener =
        Shizuku.OnRequestPermissionResultListener permListener@{
            requestCode,
            grantResult,
            ->
            requestCode.takeIf { it == PERM_REQ_CODE } ?: return@permListener
            _shizukuState.value = determineShizukuStateWhenAlive(grantResult)
        }

    private val binderReceivedListener =
        Shizuku.OnBinderReceivedListener {
            Shizuku.addRequestPermissionResultListener(permListener)
        }

    private val binderDeadListener =
        Shizuku.OnBinderDeadListener {
            _shizukuState.value = determineShizukuStateWhenDead()
            Shizuku.removeRequestPermissionResultListener(permListener)
        }

    private fun isShizukuInstalled(packageManager: PackageManager) =
        runCatching {
            packageManager.getApplicationInfo(
                ShizukuProvider.MANAGER_APPLICATION_ID,
                0,
            )
        }.getOrNull().let { it != null } ||
            Sui.isSui()

    private fun determineShizukuStateWhenAlive(permResult: Int) =
        if (permResult == PackageManager.PERMISSION_GRANTED) {
            ShizukuStates.CONNECTED
        } else {
            ShizukuStates.NOT_CONNECTED
        }

    private fun determineShizukuStateWhenDead() = when {
        isShizukuInstalled(
            application.packageManager,
        ) -> ShizukuStates.NOT_RUNNING
        else -> ShizukuStates.NOT_AVAILABLE
    }

    init {
        Sui.init(application.packageName)
        _shizukuState.value =
            when {
                Shizuku.isPreV11() -> ShizukuStates.NOT_AVAILABLE
                Shizuku.pingBinder() ->
                    determineShizukuStateWhenAlive(
                        Shizuku.checkSelfPermission(),
                    )
                else -> determineShizukuStateWhenDead()
            }
        Shizuku.addBinderReceivedListenerSticky(binderReceivedListener)
        Shizuku.addBinderDeadListener(binderDeadListener)
    }

    override fun onCleared() {
        Shizuku.removeBinderReceivedListener(binderReceivedListener)
        Shizuku.removeBinderDeadListener(binderDeadListener)
        super.onCleared()
    }

    fun requestPermission() {
        Shizuku.requestPermission(PERM_REQ_CODE)
    }

    companion object {
        private const val PERM_REQ_CODE = 2345
        const val SHIZUKU_APP_ID = ShizukuProvider.MANAGER_APPLICATION_ID
    }
}
