package dev.shadoe.delta.control.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Password
import androidx.compose.material.icons.rounded.QrCode2
import androidx.compose.material.icons.rounded.Wifi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.window.core.layout.WindowWidthSizeClass
import dev.shadoe.delta.R
import dev.shadoe.delta.api.SoftApEnabledState

@Composable
fun SoftApControl(
  onFailedToShowQr: () -> Unit,
  vm: SoftApControlViewModel = viewModel(),
) {
  val context = LocalContext.current
  val isBigScreen =
    currentWindowAdaptiveInfo().windowSizeClass.windowWidthSizeClass ==
      WindowWidthSizeClass.EXPANDED

  val ssid by vm.ssid.collectAsState("")
  val passphrase by vm.passphrase.collectAsState("")
  val shouldShowPassphrase by vm.shouldShowPassphrase.collectAsState(true)
  val enabledState by
    vm.enabledState.collectAsState(SoftApEnabledState.WIFI_AP_STATE_DISABLED)
  val shouldShowQrButton by vm.shouldShowQrButton.collectAsState(false)

  Row(
    modifier =
      Modifier.fillMaxWidth()
        .padding(horizontal = 16.dp)
        .clip(RoundedCornerShape(8.dp))
        .background(MaterialTheme.colorScheme.primaryContainer),
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.SpaceBetween,
  ) {
    Column(
      modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
      verticalArrangement = Arrangement.Center,
    ) {
      Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
          imageVector = Icons.Rounded.Wifi,
          contentDescription = stringResource(R.string.ssid_field_icon),
          modifier = Modifier.padding(end = 8.dp),
        )
        Text(text = ssid ?: stringResource(id = R.string.no_ssid))
      }
      if (shouldShowPassphrase) {
        Row(
          modifier = Modifier.padding(vertical = 8.dp),
          verticalAlignment = Alignment.CenterVertically,
        ) {
          Icon(
            imageVector = Icons.Rounded.Password,
            contentDescription = stringResource(R.string.passphrase_field_icon),
            modifier = Modifier.padding(end = 8.dp),
          )
          PassphraseDisplay(passphrase = passphrase)
        }
      }
    }
    Row(verticalAlignment = Alignment.CenterVertically) {
      if (shouldShowQrButton) {
        IconButton(
          onClick = {
            if (!vm.openQrCodeScreen(context, isBigScreen)) {
              onFailedToShowQr()
            }
          }
        ) {
          Icon(
            imageVector = Icons.Rounded.QrCode2,
            contentDescription = stringResource(id = R.string.qr_code_button),
            modifier = Modifier.size(48.dp),
          )
        }
      }
      SoftApControlButton(
        enabledState,
        startHotspot = { vm.startSoftAp() },
        stopHotspot = { vm.stopSoftAp() },
      )
    }
  }
}
