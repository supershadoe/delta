package dev.shadoe.delta.settings.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.WifiPassword
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import dev.shadoe.delta.R
import dev.shadoe.delta.api.SoftApSecurityType.SECURITY_TYPE_OPEN
import dev.shadoe.delta.api.SoftApSecurityType.SECURITY_TYPE_WPA2_PSK
import dev.shadoe.delta.api.SoftApSecurityType.SECURITY_TYPE_WPA3_SAE
import dev.shadoe.delta.api.SoftApSecurityType.SECURITY_TYPE_WPA3_SAE_TRANSITION
import dev.shadoe.delta.api.SoftApSecurityType.SecurityType

@Composable
internal fun SecurityTypeField(
  @SecurityType securityType: Int,
  supportedSecurityTypes: List<Int>,
  onSecurityTypeChange: (Int) -> Unit,
) {
  Row(
    verticalAlignment = Alignment.CenterVertically,
    modifier = Modifier.padding(vertical = 8.dp),
  ) {
    Icon(
      imageVector = Icons.Rounded.WifiPassword,
      contentDescription = stringResource(R.string.security_proto_field_icon),
    )
    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
      Text(
        text = stringResource(R.string.security_proto_field_label),
        style = MaterialTheme.typography.titleMedium,
      )
      LazyRow {
        items(supportedSecurityTypes) {
          FilterChip(
            selected = securityType == it,
            onClick = { onSecurityTypeChange(it) },
            label = {
              Text(
                text =
                  stringResource(
                    when (it) {
                      SECURITY_TYPE_OPEN -> R.string.security_proto_open
                      SECURITY_TYPE_WPA2_PSK -> R.string.security_proto_wpa2_psk
                      SECURITY_TYPE_WPA3_SAE -> R.string.security_proto_wpa3_sae
                      SECURITY_TYPE_WPA3_SAE_TRANSITION ->
                        R.string.security_proto_wpa3_sae_transition
                      else -> R.string.security_proto_not_supported
                    }
                  )
              )
            },
            modifier = Modifier.padding(horizontal = 2.dp),
          )
        }
      }
      Text(
        text =
          stringResource(
            when (securityType) {
              SECURITY_TYPE_OPEN -> R.string.security_proto_open_desc
              SECURITY_TYPE_WPA2_PSK -> R.string.security_proto_wpa2_psk_desc
              SECURITY_TYPE_WPA3_SAE -> R.string.security_proto_wpa3_sae_desc
              SECURITY_TYPE_WPA3_SAE_TRANSITION ->
                R.string.security_proto_wpa3_sae_transition_desc
              else -> R.string.security_proto_not_supported
            }
          ),
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
      )
    }
  }
}
