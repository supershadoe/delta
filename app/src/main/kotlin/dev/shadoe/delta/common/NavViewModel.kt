package dev.shadoe.delta.common

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import dev.shadoe.delta.api.ShizukuStates
import dev.shadoe.delta.crash.CrashHandlerUtils
import dev.shadoe.delta.data.FlagsRepository
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
  private val flagsRepository: FlagsRepository,
) : ViewModel() {
  private val _startScreen = MutableStateFlow<Route>(Routes.BlankScreen)
  val startScreen = _startScreen.asStateFlow()

  private suspend fun determineStartScreen(): Route {
    val isFirstRun = flagsRepository.isFirstRun()
    val shizukuConnected =
      shizukuRepository.shizukuState.value != ShizukuStates.CONNECTED
    val crashHandlerSetup =
      CrashHandlerUtils.shouldShowNotificationPermissionRequest(
        applicationContext
      )
    return when {
      isFirstRun -> Routes.Setup.FirstUseScreen
      shizukuConnected -> Routes.Setup.ShizukuSetupScreen
      crashHandlerSetup -> Routes.Setup.CrashHandlerSetupScreen
      else -> Routes.HotspotScreen
    }
  }

  init {
    addCloseable(shizukuRepository.callbackSubscriber)
    viewModelScope.launch {
      shizukuRepository.shizukuState.collect {
        _startScreen.update { determineStartScreen() }
      }
    }
  }

  fun onSetupStarted() =
    viewModelScope.launch { flagsRepository.setNotFirstRun() }

  fun onSetupFinished() =
    viewModelScope.launch { _startScreen.update { determineStartScreen() } }
}
