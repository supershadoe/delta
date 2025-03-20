package dev.shadoe.delta.setup.components

import android.content.Intent
import android.content.Intent.ACTION_VIEW
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.OpenInNew
import androidx.compose.material.icons.rounded.InstallMobile
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import dev.shadoe.delta.R

@Composable
internal fun ShizukuNotInstalled(modifier: Modifier = Modifier) {
  val context = LocalContext.current
  val isBigScreen = LocalConfiguration.current.screenWidthDp >= 700
  Column(modifier = modifier) {
    Column(
      modifier = Modifier.weight(0.5f),
      verticalArrangement = Arrangement.Center,
    ) {
      Text(
        text = stringResource(R.string.shizuku_setup_title),
        style = MaterialTheme.typography.displaySmall,
      )
      Text(
        text = stringResource(R.string.shizuku_setup_not_installed),
        style = MaterialTheme.typography.titleLarge,
      )
    }
    Column(
      modifier = Modifier.weight(0.7f).fillMaxWidth(),
      horizontalAlignment = Alignment.End,
    ) {
      Button(
        onClick = {
          context.startActivity(
            Intent(
                ACTION_VIEW,
                "https://github.com/RikkaApps/Shizuku/releases".toUri(),
              )
              .apply {
                if (isBigScreen) {
                  addFlags(
                    Intent.FLAG_ACTIVITY_LAUNCH_ADJACENT or
                      Intent.FLAG_ACTIVITY_NEW_TASK
                  )
                }
              }
          )
        }
      ) {
        Icon(
          imageVector = Icons.AutoMirrored.Rounded.OpenInNew,
          contentDescription = stringResource(R.string.open_icon),
        )
        Text(
          text = stringResource(R.string.github),
          modifier = Modifier.padding(start = 8.dp),
        )
      }
      Button(
        onClick = {
          context.startActivity(
            Intent(ACTION_VIEW).apply {
              data =
                "https://play.google.com/store/apps/details?id=moe.shizuku.privileged.api"
                  .toUri()
              `package` = "com.android.vending"
              if (isBigScreen) {
                addFlags(
                  Intent.FLAG_ACTIVITY_LAUNCH_ADJACENT or
                    Intent.FLAG_ACTIVITY_NEW_TASK
                )
              }
            }
          )
        }
      ) {
        Icon(
          imageVector = Icons.Rounded.InstallMobile,
          contentDescription = stringResource(R.string.install_from_icon),
        )
        Text(
          text = stringResource(R.string.play_store),
          modifier = Modifier.padding(start = 8.dp),
        )
      }
    }
    Text(
      text = stringResource(R.string.setup_note),
      style = MaterialTheme.typography.bodySmall,
      color = MaterialTheme.colorScheme.onSurfaceVariant,
      modifier = Modifier.padding(bottom = 32.dp),
    )
  }
}
