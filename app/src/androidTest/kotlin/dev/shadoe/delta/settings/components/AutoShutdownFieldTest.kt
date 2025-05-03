package dev.shadoe.delta.settings.components

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.assertIsOff
import androidx.compose.ui.test.assertIsOn
import androidx.compose.ui.test.isToggleable
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlin.test.Test
import org.junit.Rule
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AutoShutdownFieldTest {
  @get:Rule val composeTestRule = createComposeRule()

  @Test
  fun `Check if the switch invokes the callback on tap`() {
    composeTestRule.run {
      setContent {
        var state by remember { mutableStateOf(false) }
        AutoShutdownField(
          isAutoShutdownEnabled = state,
          onAutoShutdownChange = { state = !state },
        )
      }
      onNode(isToggleable()).run {
        performClick()
        assertIsOn()
        performClick()
        assertIsOff()
      }
    }
  }
}
