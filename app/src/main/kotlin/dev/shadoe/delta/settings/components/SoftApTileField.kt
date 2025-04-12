package dev.shadoe.delta.settings.components

import android.app.StatusBarManager
import android.content.ComponentName
import android.content.Context
import android.graphics.drawable.Icon
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.SettingsSuggest
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import dev.shadoe.delta.R
import dev.shadoe.delta.SoftApTile

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
private fun addTileToQuickSettings(context: Context) {
  val sbm = context.getSystemService(StatusBarManager::class.java)
  sbm.requestAddTileService(
    ComponentName(context.packageName, SoftApTile::class.java.name),
    context.getString(R.string.tile_title),
    Icon.createWithResource(context, R.drawable.ic_launcher_foreground),
    context.mainExecutor,
  ) {}
}

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
internal fun SoftApTileField() {
  val context = LocalContext.current
  Row(
    modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp),
    verticalAlignment = Alignment.CenterVertically,
  ) {
    Icon(
      imageVector = Icons.Rounded.SettingsSuggest,
      contentDescription = stringResource(R.string.tile_field_icon),
    )
    Column(
      modifier = Modifier.weight(1f).padding(horizontal = 16.dp),
      horizontalAlignment = Alignment.Start,
    ) {
      Text(
        text = stringResource(R.string.tile_field_label),
        style = MaterialTheme.typography.titleLarge,
      )
      Text(
        text = stringResource(R.string.tile_field_desc),
        modifier = Modifier.padding(vertical = 4.dp),
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
      )
      Button(onClick = { addTileToQuickSettings(context) }) {
        Text(text = stringResource(R.string.tile_add_button))
      }
    }
  }
}
