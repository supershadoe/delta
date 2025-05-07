package dev.shadoe.delta.common.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.runtime.Composable

@Composable
fun FadeInExpanded(
  visible: Boolean,
  content: @Composable AnimatedVisibilityScope.() -> Unit,
) {
  AnimatedVisibility(
    visible = visible,
    enter = fadeIn() + expandVertically(),
    exit = fadeOut() + shrinkVertically(),
    content = content,
  )
}
