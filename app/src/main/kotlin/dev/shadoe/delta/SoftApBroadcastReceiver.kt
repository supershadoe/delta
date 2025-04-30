package dev.shadoe.delta

import android.Manifest
import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationChannelCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import dagger.hilt.android.AndroidEntryPoint
import dev.shadoe.delta.api.ShizukuStates
import dev.shadoe.delta.data.FlagsRepository
import dev.shadoe.delta.data.shizuku.ShizukuRepository
import dev.shadoe.delta.data.softap.SoftApControlRepository
import dev.shadoe.delta.data.softap.SoftApStateFacade
import javax.inject.Inject
import kotlinx.coroutines.runBlocking

@AndroidEntryPoint
class SoftApBroadcastReceiver : BroadcastReceiver() {
  private var shizukuCallback: AutoCloseable? = null

  companion object {
    private const val SOFT_AP_FAILURE_CHANNEL_ID =
      "dev.shadoe.delta.softApFailure"
    private const val SOFT_AP_FAILURE_NOTIF_ID = 2
    const val ACTION_START_SOFT_AP = "dev.shadoe.delta.action.START_SOFT_AP"
    const val ACTION_STOP_SOFT_AP = "dev.shadoe.delta.action.STOP_SOFT_AP"
  }

  @Inject lateinit var shizukuRepository: ShizukuRepository
  @Inject lateinit var softApControlRepository: SoftApControlRepository
  @Inject lateinit var flagsRepository: FlagsRepository
  @Inject lateinit var softApStateFacade: SoftApStateFacade

  fun sendFailureNotification(context: Context) {
    val notification =
      NotificationCompat.Builder(context, SOFT_AP_FAILURE_CHANNEL_ID)
        .setContentTitle(
          context.getString(R.string.soft_ap_failure_notif_title)
        )
        .setContentText(
          context.getString(R.string.soft_ap_failure_notif_content)
        )
        .setSmallIcon(R.drawable.ic_launcher_foreground)
        .setPriority(NotificationCompat.PRIORITY_MAX)
        .build()

    val canSend =
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        ActivityCompat.checkSelfPermission(
          context,
          Manifest.permission.POST_NOTIFICATIONS,
        ) == PackageManager.PERMISSION_GRANTED
      } else {
        true
      }

    if (!canSend) return

    NotificationManagerCompat.from(context).run {
      if (getNotificationChannel(SOFT_AP_FAILURE_CHANNEL_ID) == null) {
        val notifChannel =
          NotificationChannelCompat.Builder(
              SOFT_AP_FAILURE_CHANNEL_ID,
              NotificationManagerCompat.IMPORTANCE_HIGH,
            )
            .setName(
              context.getString(R.string.soft_ap_failure_notif_channel_name)
            )
            .setDescription(
              context.getString(R.string.soft_ap_failure_notif_channel_desc)
            )
            .build()
        createNotificationChannel(notifChannel)
      }

      @SuppressLint("MissingPermission")
      notify(SOFT_AP_FAILURE_NOTIF_ID, notification)
    }
  }

  fun cleanup() {
    softApStateFacade.stop()
    shizukuCallback?.close()
  }

  override fun onReceive(context: Context, intent: Intent) {
    if (intent.action !in setOf(ACTION_START_SOFT_AP, ACTION_STOP_SOFT_AP))
      return

    val isReceiverEnabled = runBlocking {
      flagsRepository.isInsecureReceiverEnabled()
    }
    if (!isReceiverEnabled) return

    shizukuCallback = shizukuRepository.callbackSubscriber
    if (shizukuRepository.shizukuState.value != ShizukuStates.CONNECTED) {
      sendFailureNotification(context)
      cleanup()
      return
    }

    softApStateFacade.start()
    when (intent.action) {
      ACTION_START_SOFT_AP -> softApControlRepository.startSoftAp()
      ACTION_STOP_SOFT_AP -> softApControlRepository.stopSoftAp()
    }
    cleanup()
  }
}
