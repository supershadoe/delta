package dev.shadoe.delta.shizuku.components

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import dev.shadoe.delta.R
import dev.shadoe.delta.shizuku.ShizukuViewModel
import rikka.shizuku.Shizuku

@Composable
internal fun ShizukuNotConnected() {
    Column {
        Text(
            stringResource(R.string.shizuku_not_connected_desc),
        )
        Button(onClick = {
            Shizuku.requestPermission(ShizukuViewModel.PERM_REQ_CODE)
        }) {
            Text(text = stringResource(R.string.shizuku_grant_access))
        }
    }
}
