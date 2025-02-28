package dev.shadoe.delta.shizuku.components

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import dev.shadoe.delta.R

@Composable
internal fun ShizukuNotConnected(onRequestPermission: () -> Unit) {
  Column {
    Text(stringResource(R.string.shizuku_not_connected_desc))
    Button(onClick = { onRequestPermission() }) {
      Text(text = stringResource(R.string.shizuku_grant_access))
    }
  }
}
