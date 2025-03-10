package dev.shadoe.delta.common

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import dev.shadoe.delta.api.ShizukuStates
import dev.shadoe.delta.crash.CrashHandlerUtils
import dev.shadoe.delta.data.shizuku.ShizukuRepository
import dev.shadoe.delta.data.softap.SoftApRepository
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
  private val softApRepository: SoftApRepository,
) : ViewModel() {
  private var softApCloseable: AutoCloseable? = null

  private val _isSetupNeeded = MutableStateFlow(false)
  val isSetupNeeded = _isSetupNeeded.asStateFlow()

  private fun determineIfSetupNeeded(): Boolean {
    val shizukuConnected =
      shizukuRepository.shizukuState.value != ShizukuStates.CONNECTED
    val crashHandlerSetup =
      CrashHandlerUtils.shouldShowNotificationPermissionRequest(
        applicationContext
      )
    return shizukuConnected || crashHandlerSetup
  }

  init {
    addCloseable(shizukuRepository.viewModelHook())
    viewModelScope.launch {
      shizukuRepository.shizukuState.collect {
        softApCloseable?.close()
        softApCloseable =
          if (it == ShizukuStates.CONNECTED) {
            softApRepository.viewModelHook(viewModelScope)
          } else {
            null
          }
        _isSetupNeeded.update { determineIfSetupNeeded() }
      }
    }
  }

  override fun onCleared() {
    softApCloseable?.close()
    super.onCleared()
  }
}
