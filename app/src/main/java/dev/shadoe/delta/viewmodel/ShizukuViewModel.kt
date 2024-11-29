package dev.shadoe.delta.viewmodel

import android.content.pm.PackageManager
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import rikka.shizuku.Shizuku

class ShizukuViewModel : ViewModel() {
    private val _isConnected = MutableLiveData(false)
    val isConnected: LiveData<Boolean> = _isConnected
    private val _isRunning = MutableLiveData(false)
    val isRunning: LiveData<Boolean> = _isRunning

    companion object {
        private const val PERM_REQ_CODE = 2345
    }

    private fun checkShizukuPerm(): Boolean = when {
        Shizuku.isPreV11() -> false
        Shizuku.checkSelfPermission() == PackageManager.PERMISSION_GRANTED -> true
        Shizuku.shouldShowRequestPermissionRationale() -> false
        else -> {
            Shizuku.requestPermission(PERM_REQ_CODE)
            false
        }
    }

    private val permListener =
        Shizuku.OnRequestPermissionResultListener { requestCode, grantResult ->
            if (requestCode == PERM_REQ_CODE) {
                _isConnected.postValue(
                    grantResult == PackageManager.PERMISSION_GRANTED
                )
            }
        }

    private val binderReceivedListener = Shizuku.OnBinderReceivedListener {
        _isRunning.postValue(true)
        Shizuku.addRequestPermissionResultListener(permListener)
        _isConnected.postValue(checkShizukuPerm())
    }

    private val binderDeadListener = Shizuku.OnBinderDeadListener {
        _isRunning.postValue(false)
        Shizuku.removeRequestPermissionResultListener(permListener)
    }

    init {
        _isRunning.value = Shizuku.pingBinder()
        if (_isRunning.value == true) {
            _isConnected.value = checkShizukuPerm()
        }
        Shizuku.addBinderReceivedListenerSticky(binderReceivedListener)
        Shizuku.addBinderDeadListener(binderDeadListener)
    }

    override fun onCleared() {
        Shizuku.removeBinderReceivedListener(binderReceivedListener)
        Shizuku.removeBinderDeadListener(binderDeadListener)
        super.onCleared()
    }
}