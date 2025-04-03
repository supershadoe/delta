package dev.shadoe.delta.setup

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.graphics.shapes.CornerRounding
import androidx.graphics.shapes.RoundedPolygon
import androidx.graphics.shapes.pillStar
import dev.shadoe.delta.R
import dev.shadoe.delta.common.shapes.PolygonShape

@Composable
fun FirstUseScreen(onStartSetup: () -> Unit) {
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
            painter = painterResource(R.drawable.ic_launcher_foreground),
            contentDescription = stringResource(R.string.app_icon),
            modifier = Modifier.size(150.dp).align(Alignment.BottomStart),
          )
        }
      }
      Column {
        Text(
          text = stringResource(R.string.app_name),
          style = MaterialTheme.typography.displaySmall,
        )
        Text(
          text = stringResource(R.string.app_tagline),
          style = MaterialTheme.typography.titleLarge,
        )
      }
      Box(
        modifier = Modifier.weight(1f).fillMaxWidth(),
        contentAlignment = Alignment.CenterEnd,
      ) {
        Button(onClick = onStartSetup) {
          Text(
            text = stringResource(R.string.setup_start_button),
            fontWeight = FontWeight.SemiBold,
          )
        }
      }
      Text(
        text = stringResource(R.string.setup_note),
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.padding(bottom = 32.dp),
      )
    }
  }
}
