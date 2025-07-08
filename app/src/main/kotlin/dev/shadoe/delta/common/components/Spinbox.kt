package dev.shadoe.delta.common.components

import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Remove
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import dev.shadoe.delta.R

@Composable
fun Spinbox(
  modifier: Modifier = Modifier,
  currentValue: Int?,
  min: Int,
  max: Int,
  onValueChanged: (Int?) -> Unit,
  label: (@Composable () -> Unit)? = null,
  step: Int = 1,
) {
  OutlinedTextField(
    modifier = modifier,
    value = (currentValue ?: "").toString(),
    label = label,
    onValueChange = { onValueChanged(it.toIntOrNull()?.coerceIn(min, max)) },
    keyboardOptions =
      KeyboardOptions(
        autoCorrectEnabled = false,
        keyboardType = KeyboardType.Number,
      ),
    singleLine = true,
    leadingIcon = {
      IconButton(
        onClick = { onValueChanged(currentValue?.minus(step)) },
        enabled = (currentValue?.compareTo(min) ?: 0) > 0,
        content = {
          Icon(
            imageVector = Icons.Outlined.Remove,
            contentDescription = stringResource(R.string.decrement),
          )
        },
      )
    },
    trailingIcon = {
      IconButton(
        onClick = { onValueChanged(currentValue?.plus(step)) },
        enabled = (currentValue?.compareTo(max) ?: 0) < 0,
        content = {
          Icon(
            imageVector = Icons.Outlined.Add,
            contentDescription = stringResource(R.string.increment),
          )
        },
      )
    },
  )
}
