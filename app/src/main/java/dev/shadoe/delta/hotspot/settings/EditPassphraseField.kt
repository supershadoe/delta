package dev.shadoe.delta.hotspot.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Password
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

@Composable
internal fun EditPassphraseField(
    value: String,
    onSave: (String) -> Unit = {},
) {
    val isEditing = remember { mutableStateOf(false) }
    val textFieldState = remember(value) { mutableStateOf(value) }
    val scope = rememberCoroutineScope()
    val onDone = {
        scope.launch {
            onSave(textFieldState.value)
        }
        isEditing.value = false
    }
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                onClickLabel = "Edit Passphrase", role = Role.Button,
            ) {
                isEditing.value = true
            },
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                imageVector = Icons.Rounded.Password,
                contentDescription = "Passphrase icon"
            )
            Column(modifier = Modifier.padding(start = 16.dp)) {
                Text(text = "Passphrase")
                Text(text = "********")
            }
        }
    }
    if (isEditing.value) AlertDialog(
        onDismissRequest = {
            textFieldState.value = value
            isEditing.value = false
        },
        title = { Text(text = "Edit Passphrase") },
        text = {
            TextField(
                value = textFieldState.value,
                onValueChange = { textFieldState.value = it },
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.None,
                    autoCorrectEnabled = false,
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Done,
                ),
                keyboardActions = KeyboardActions(onDone = { onDone() }),
            )
        },
        confirmButton = {
            TextButton(onClick = onDone) {
                Text(text = "Save")
            }
        },
    )
}