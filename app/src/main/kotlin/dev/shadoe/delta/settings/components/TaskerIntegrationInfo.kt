package dev.shadoe.delta.settings.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.CompareArrows
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.fromHtml
import dev.shadoe.delta.R
import dev.shadoe.delta.SoftApBroadcastReceiver

@Composable
fun TaskerIntegrationInfo(onDismissDialog: () -> Unit) {
  val clipboardManager = LocalClipboardManager.current
  val context = LocalContext.current
  AlertDialog(
    onDismissRequest = onDismissDialog,
    confirmButton = {
      TextButton(onClick = onDismissDialog) {
        Text(stringResource(R.string.close_button))
      }
    },
    icon = {
      Icon(
        imageVector = Icons.AutoMirrored.Rounded.CompareArrows,
        contentDescription =
          stringResource(R.string.tasker_integration_field_icon),
      )
    },
    title = {
      Text(text = stringResource(R.string.tasker_integration_field_label))
    },
    text = {
      LazyColumn {
        item {
          Text(
            text =
              AnnotatedString.fromHtml(
                stringResource(R.string.tasker_integration_info)
              )
          )
        }
        item {
          Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.CenterEnd,
          ) {
            TextButton(
              onClick = {
                clipboardManager.setText(AnnotatedString(context.packageName))
              }
            ) {
              Text(
                text =
                  stringResource(R.string.tasker_integration_info_copy_pkg_name)
              )
            }
          }
        }
        item {
          Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.CenterEnd,
          ) {
            TextButton(
              onClick = {
                clipboardManager.setText(
                  AnnotatedString(SoftApBroadcastReceiver::class.java.name)
                )
              }
            ) {
              Text(
                text =
                  stringResource(R.string.tasker_integration_info_copy_cls_name)
              )
            }
          }
        }
        item {
          Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.CenterEnd,
          ) {
            TextButton(
              onClick = {
                clipboardManager.setText(
                  AnnotatedString(SoftApBroadcastReceiver.ACTION_STOP_SOFT_AP)
                )
              }
            ) {
              Text(
                AnnotatedString.fromHtml(
                  stringResource(
                    R.string.tasker_integration_info_copy_stop_action
                  )
                )
              )
            }
          }
        }
        item {
          Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.CenterEnd,
          ) {
            TextButton(
              onClick = {
                clipboardManager.setText(
                  AnnotatedString(SoftApBroadcastReceiver.ACTION_START_SOFT_AP)
                )
              }
            ) {
              Text(
                AnnotatedString.fromHtml(
                  stringResource(
                    R.string.tasker_integration_info_copy_start_action
                  )
                )
              )
            }
          }
        }
      }
    },
  )
}
