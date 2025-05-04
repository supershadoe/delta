package dev.shadoe.delta.control.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import dev.shadoe.delta.R
import dev.shadoe.delta.api.ACLDevice
import dev.shadoe.delta.common.components.FoldableWrapper

internal data class BlocklistComponentState(
  val isBlocklistShown: Boolean,
  val blockedClients: List<ACLDevice>,
  val devicesToUnblock: Set<ACLDevice>,
)

internal data class BlocklistComponentActions(
  val addToUnblockList: (ACLDevice) -> Unit,
  val removeFromUnblockList: (ACLDevice) -> Unit,
  val onBlocklistToggled: () -> Unit,
  val onUnblockClients: () -> Unit,
)

internal fun LazyListScope.blocklistComponent(
  state: BlocklistComponentState,
  actions: BlocklistComponentActions,
) {
  item {
    FoldableWrapper(
      text = stringResource(R.string.blocklist),
      foldableState = state.isBlocklistShown,
      onFoldableToggled = actions.onBlocklistToggled,
      modifier = Modifier.padding(horizontal = 16.dp),
    )
  }
  item {
    AnimatedVisibility(
      visible = state.isBlocklistShown && state.blockedClients.isEmpty(),
      enter = fadeIn() + expandVertically(),
      exit = fadeOut() + shrinkVertically(),
    ) {
      Box(
        modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
        contentAlignment = Alignment.Center,
      ) {
        Text(text = stringResource(R.string.blocklist_none_blocked))
      }
    }
  }
  items(state.blockedClients) { client ->
    AnimatedVisibility(
      visible = state.isBlocklistShown,
      enter = fadeIn() + expandVertically(),
      exit = fadeOut() + shrinkVertically(),
    ) {
      BlockedClientComponent(
        state =
          BlockedClientComponentState(
            device = client,
            isEditingBlocklist = state.devicesToUnblock.isNotEmpty(),
            isChosenForUnblock = state.devicesToUnblock.contains(client),
          ),
        actions =
          BlockedClientComponentActions(
            startEditing = { actions.addToUnblockList(client) },
            onEditToggled = { isChecked ->
              if (isChecked) {
                actions.addToUnblockList(client)
              } else {
                actions.removeFromUnblockList(client)
              }
            },
          ),
      )
    }
  }
  item {
    AnimatedVisibility(
      visible = state.devicesToUnblock.isNotEmpty(),
      enter = fadeIn() + expandVertically(),
      exit = fadeOut() + shrinkVertically(),
    ) {
      Button(
        onClick = actions.onUnblockClients,
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
      ) {
        Text(text = stringResource(R.string.unblock_button))
      }
    }
  }
}
