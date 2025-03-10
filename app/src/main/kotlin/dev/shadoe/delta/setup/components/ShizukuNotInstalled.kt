package dev.shadoe.delta.setup.components

import android.content.Intent
import android.content.Intent.ACTION_VIEW
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.OpenInNew
import androidx.compose.material.icons.rounded.InstallMobile
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import dev.shadoe.delta.R

@Composable
internal fun ShizukuNotInstalled() {
  val context = LocalContext.current
  Column {
    Text(stringResource(R.string.shizuku_not_installed))
    Button(
      onClick = {
        context.startActivity(
          Intent(
            ACTION_VIEW,
            "https://github.com/RikkaApps/Shizuku/releases".toUri(),
          )
        )
      }
    ) {
      Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
          imageVector = Icons.AutoMirrored.Rounded.OpenInNew,
          contentDescription = stringResource(R.string.open_icon),
        )
        Text(
          text = stringResource(R.string.github),
          modifier = Modifier.padding(start = 8.dp),
        )
      }
    }
    Button(
      onClick = {
        context.startActivity(
          Intent(ACTION_VIEW).apply {
            data =
              "https://play.google.com/store/apps/details?id=moe.shizuku.privileged.api"
                .toUri()
            `package` = "com.android.vending"
          }
        )
      }
    ) {
      Row(verticalAlignment = Alignment.CenterVertically) {
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
  }
}
