package dev.shadoe.delta.setup

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.graphics.shapes.CornerRounding
import androidx.graphics.shapes.RoundedPolygon
import androidx.graphics.shapes.pillStar
import androidx.lifecycle.viewmodel.compose.viewModel
import dev.shadoe.delta.R
import dev.shadoe.delta.api.ShizukuStates.CONNECTED
import dev.shadoe.delta.api.ShizukuStates.NOT_AVAILABLE
import dev.shadoe.delta.api.ShizukuStates.NOT_CONNECTED
import dev.shadoe.delta.api.ShizukuStates.NOT_RUNNING
import dev.shadoe.delta.common.LocalNavController
import dev.shadoe.delta.common.Routes
import dev.shadoe.delta.common.shapes.PolygonShape
import dev.shadoe.delta.setup.components.ShizukuConnected
import dev.shadoe.delta.setup.components.ShizukuNotConnected
import dev.shadoe.delta.setup.components.ShizukuNotInstalled
import dev.shadoe.delta.setup.components.ShizukuNotRunning

@Composable
fun ShizukuSetupScreen(vm: ShizukuSetupViewModel = viewModel()) {
  val navController = LocalNavController.current
  val state by vm.shizukuState.collectAsState()
  val roundedPillStar = remember {
    RoundedPolygon.pillStar(
      width = 1f,
      height = 1f,
      innerRadiusRatio = 0.9f,
      numVerticesPerRadius = 12,
      rounding = CornerRounding(0.1f),
    )
  }
  val shape = remember(roundedPillStar) { PolygonShape(roundedPillStar) }
  Scaffold {
    Column(
      modifier = Modifier.fillMaxSize().padding(it).padding(horizontal = 16.dp)
    ) {
      Box(
        modifier = Modifier.weight(3f).fillMaxWidth(),
        contentAlignment = Alignment.CenterEnd,
      ) {
        Box(
          modifier =
            Modifier.size(150.dp)
              .background(
                color = MaterialTheme.colorScheme.surfaceVariant,
                shape = shape,
              )
        ) {
          Icon(
            painter = painterResource(R.drawable.shizuku_logo_mono),
            contentDescription = stringResource(R.string.shizuku_icon),
            modifier = Modifier.size(150.dp).align(Alignment.BottomStart),
          )
        }
      }
      Box(modifier = Modifier.weight(2f)) {
        when (state) {
          NOT_AVAILABLE -> ShizukuNotInstalled()
          NOT_RUNNING -> ShizukuNotRunning()
          NOT_CONNECTED ->
            ShizukuNotConnected(
              onRequestPermission = { vm.requestPermission() }
            )
          CONNECTED ->
            ShizukuConnected(
              continueAction = {
                navController?.navigate(
                  route = Routes.Setup.CrashHandlerSetupScreen
                )
              }
            )
          else -> {}
        }
      }
    }
    Column(modifier = Modifier.padding(it)) {}
  }
}
