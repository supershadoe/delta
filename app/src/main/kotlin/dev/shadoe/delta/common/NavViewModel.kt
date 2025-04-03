package dev.shadoe.delta.common

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import dev.shadoe.delta.api.ShizukuStates
import dev.shadoe.delta.crash.CrashHandlerUtils
import dev.shadoe.delta.data.shizuku.ShizukuRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class NavViewModel
@Inject
constructor(
  @ApplicationContext private val applicationContext: Context,
  private val shizukuRepository: ShizukuRepository,
) : ViewModel() {
  private val _startScreen =
    MutableStateFlow<Route>(Routes.Setup.FirstUseScreen)
  val startScreen = _startScreen.asStateFlow()

  private fun determineIfSetupNeeded(): Route {
    val shizukuConnected =
      shizukuRepository.shizukuState.value != ShizukuStates.CONNECTED
    val crashHandlerSetup =
      CrashHandlerUtils.shouldShowNotificationPermissionRequest(
        applicationContext
      )
    return if (shizukuConnected) {
      Routes.Setup.ShizukuSetupScreen
    } else if (crashHandlerSetup) {
      Routes.Setup.CrashHandlerSetupScreen
    } else {
      Routes.HotspotScreen
    }
  }

  init {
    addCloseable(shizukuRepository.callbackSubscriber)
    viewModelScope.launch {
      shizukuRepository.shizukuState.collect {
        _startScreen.update { determineIfSetupNeeded() }
      }
    }
  }

  fun onSetupFinished() = _startScreen.update { determineIfSetupNeeded() }
}
