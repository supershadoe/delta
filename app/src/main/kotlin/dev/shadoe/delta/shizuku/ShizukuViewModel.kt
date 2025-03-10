package dev.shadoe.delta.shizuku

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.shadoe.delta.data.shizuku.ShizukuRepository
import javax.inject.Inject

@HiltViewModel
class ShizukuViewModel
@Inject
constructor(private val shizukuRepository: ShizukuRepository) : ViewModel() {
  @dev.shadoe.delta.data.shizuku.ShizukuStates.ShizukuStateType
  val shizukuState = shizukuRepository.shizukuState

  init {
    addCloseable(shizukuRepository.viewModelHook())
  }

  fun requestPermission() = shizukuRepository.requestPermission()

  companion object {
    const val SHIZUKU_APP_ID = ShizukuRepository.SHIZUKU_APP_ID
  }
}
