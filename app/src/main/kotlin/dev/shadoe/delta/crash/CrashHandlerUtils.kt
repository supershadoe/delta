package dev.shadoe.delta.crash

import android.Manifest
import android.annotation.SuppressLint
import android.app.ActivityOptions
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationChannelCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.app.PendingIntentCompat
import dev.shadoe.delta.R

object CrashHandlerUtils {
  private const val CRASH_CHANNEL_ID = "dev.shadoe.delta.crashChannel"
  const val CRASH_NOTIF_ID = 1

  fun shouldShowNotificationPermissionRequest(
    applicationContext: Context
  ): Boolean {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) return false

    return ActivityCompat.checkSelfPermission(
      applicationContext,
      Manifest.permission.POST_NOTIFICATIONS,
    ) != PackageManager.PERMISSION_GRANTED
  }

  fun sendCrashNotification(applicationContext: Context, throwable: Throwable) {
    val intent =
      Intent(applicationContext, CrashHandlerActivity::class.java).apply {
        putExtra(
          CrashHandlerActivity.EXTRA_CRASH_INFO,
          throwable.stackTraceToString(),
        )
        flags = Intent.FLAG_ACTIVITY_NEW_TASK
      }

    val pendingIntent =
      PendingIntentCompat.getActivity(
        applicationContext,
        0,
        intent,
        PendingIntent.FLAG_UPDATE_CURRENT,
        ActivityOptions.makeBasic()
          .apply {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
              setPendingIntentCreatorBackgroundActivityStartMode(
                ActivityOptions.MODE_BACKGROUND_ACTIVITY_START_ALLOWED
              )
            }
          }
          .toBundle(),
        false,
      )

    val reportAction =
      NotificationCompat.Action.Builder(
          R.drawable.ic_launcher_foreground,
          applicationContext.getString(R.string.crash_notif_action),
          pendingIntent,
        )
        .build()

    val notification =
      NotificationCompat.Builder(applicationContext, CRASH_CHANNEL_ID)
        .setContentTitle(
          applicationContext.getString(R.string.crash_notif_title)
        )
        .setSubText(applicationContext.getString(R.string.crash_notif_sub_text))
        .setContentText(
          applicationContext.getString(R.string.crash_notif_content)
        )
        .setSmallIcon(R.drawable.ic_launcher_foreground)
        .setPriority(NotificationCompat.PRIORITY_MAX)
        .setContentIntent(pendingIntent)
        .addAction(reportAction)
        .build()

    val canSend =
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        ActivityCompat.checkSelfPermission(
          applicationContext,
          Manifest.permission.POST_NOTIFICATIONS,
        ) == PackageManager.PERMISSION_GRANTED
      } else {
        true
      }

    if (!canSend) return

    NotificationManagerCompat.from(applicationContext).run {
      if (getNotificationChannel(CRASH_CHANNEL_ID) == null) {
        val notifChannel =
          NotificationChannelCompat.Builder(
              CRASH_CHANNEL_ID,
              NotificationManagerCompat.IMPORTANCE_HIGH,
            )
            .setName(
              applicationContext.getString(R.string.crash_notif_channel_name)
            )
            .build()
        createNotificationChannel(notifChannel)
      }

      @SuppressLint("MissingPermission") notify(CRASH_NOTIF_ID, notification)
    }
  }
}
