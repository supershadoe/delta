package dev.shadoe.delta.data.shizuku

import android.content.Context
import android.content.pm.PackageManager
import dagger.hilt.android.qualifiers.ApplicationContext
import dev.shadoe.delta.api.ShizukuStates
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import rikka.shizuku.Shizuku
import rikka.shizuku.ShizukuProvider
import rikka.sui.Sui

@Singleton
class ShizukuRepository
@Inject
constructor(@ApplicationContext private val applicationContext: Context) {
  companion object {
    private const val PERM_REQ_CODE = 2345
    const val SHIZUKU_APP_ID = ShizukuProvider.MANAGER_APPLICATION_ID
  }

  @ShizukuStates.ShizukuStateType
  private val _shizukuState = MutableStateFlow(ShizukuStates.NOT_READY)

  private val permListener =
    Shizuku.OnRequestPermissionResultListener { requestCode, grantResult ->
      if (requestCode == PERM_REQ_CODE) {
        _shizukuState.value = determineShizukuStateWhenAlive(grantResult)
      }
    }

  private val binderReceivedListener =
    Shizuku.OnBinderReceivedListener {
      _shizukuState.value =
        determineShizukuStateWhenAlive(Shizuku.checkSelfPermission())
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
      }
      .getOrNull()
      .let { it != null } || Sui.isSui()

  private fun determineShizukuStateWhenAlive(permResult: Int) =
    if (permResult == PackageManager.PERMISSION_GRANTED) {
      ShizukuStates.CONNECTED
    } else {
      ShizukuStates.NOT_CONNECTED
    }

  private fun determineShizukuStateWhenDead() =
    when {
      isShizukuInstalled(applicationContext.packageManager) ->
        ShizukuStates.NOT_RUNNING
      else -> ShizukuStates.NOT_AVAILABLE
    }

  init {
    Sui.init(applicationContext.packageName)
    _shizukuState.value =
      when {
        Shizuku.isPreV11() -> ShizukuStates.NOT_AVAILABLE
        Shizuku.pingBinder() ->
          determineShizukuStateWhenAlive(Shizuku.checkSelfPermission())
        else -> determineShizukuStateWhenDead()
      }
  }

  @ShizukuStates.ShizukuStateType val shizukuState = _shizukuState.asStateFlow()

  inner class CallbackSubscriber internal constructor() : AutoCloseable {
    init {
      Shizuku.addBinderReceivedListenerSticky(binderReceivedListener)
      Shizuku.addBinderDeadListener(binderDeadListener)
    }

    override fun close() {
      Shizuku.removeBinderReceivedListener(binderReceivedListener)
      Shizuku.removeBinderDeadListener(binderDeadListener)
    }
  }

  val callbackSubscriber
    get() = CallbackSubscriber()

  fun requestPermission() {
    Shizuku.requestPermission(PERM_REQ_CODE)
  }
}
