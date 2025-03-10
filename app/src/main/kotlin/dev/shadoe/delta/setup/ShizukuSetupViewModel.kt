package dev.shadoe.delta.setup

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.shadoe.delta.data.shizuku.ShizukuRepository
import javax.inject.Inject

@HiltViewModel
class ShizukuSetupViewModel
@Inject
constructor(private val shizukuRepository: ShizukuRepository) : ViewModel() {
  val shizukuState = shizukuRepository.shizukuState

  fun requestPermission() {
    shizukuRepository.requestPermission()
  }

  companion object {
    const val SHIZUKU_APP_ID = ShizukuRepository.SHIZUKU_APP_ID
  }
}
