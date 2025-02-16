package dev.shadoe.delta.hotspot.settings

import android.net.wifi.SoftApConfiguration
import android.net.wifi.SoftApConfiguration.SecurityType
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.WifiPassword
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

private fun getNameOfSecurityType(@SecurityType securityType: Int): String {
    return when (securityType) {
        SoftApConfiguration.SECURITY_TYPE_OPEN -> "None"
        SoftApConfiguration.SECURITY_TYPE_WPA2_PSK -> "WPA2-Personal"
        SoftApConfiguration.SECURITY_TYPE_WPA3_SAE -> "WPA3-Personal"
        SoftApConfiguration.SECURITY_TYPE_WPA3_SAE_TRANSITION -> "WPA2/WPA3-Personal"
        else -> "Not supported"
    }
}

@Composable
internal fun EditSecurityType(
    @SecurityType value: Int,
    onSave: (Int) -> Unit = {},
) {
    val isEditing = remember { mutableStateOf(false) }
    val selectedType = remember(value) { mutableIntStateOf(value) }
    val scope = rememberCoroutineScope()
    val supportedSecurityTypes = remember {
        intArrayOf(
            SoftApConfiguration.SECURITY_TYPE_WPA3_SAE_TRANSITION,
            SoftApConfiguration.SECURITY_TYPE_WPA3_SAE,
            SoftApConfiguration.SECURITY_TYPE_WPA2_PSK,
            SoftApConfiguration.SECURITY_TYPE_OPEN,
        )
    }
    val onDone = {
        scope.launch {
            onSave(selectedType.intValue)
        }
        isEditing.value = false
    }
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                onClickLabel = "Edit Security Type", role = Role.Button,
            ) {
                isEditing.value = true
            },
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                imageVector = Icons.Rounded.WifiPassword,
                contentDescription = "Security Type icon"
            )
            Column(modifier = Modifier.padding(start = 16.dp)) {
                Text(text = "Security Type")
                Text(text = getNameOfSecurityType(selectedType.intValue))
            }
        }
    }
    if (isEditing.value) AlertDialog(
        onDismissRequest = {
            selectedType.intValue = value
            isEditing.value = false
        },
        title = { Text(text = "Edit security type") },
        text = {
            LazyColumn {
                items(supportedSecurityTypes.size) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        RadioButton(
                            selected = selectedType.intValue == it,
                            onClick = { selectedType.intValue = it },
                        )
                        Text(text = getNameOfSecurityType(it))
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDone) {
                Text(text = "Save")
            }
        },
    )
}