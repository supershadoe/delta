import com.diffplug.spotless.kotlin.KtfmtStep.TrailingCommaManagementStrategy

plugins { id("com.diffplug.spotless") }

spotless {
  kotlin {
    target("src/**/*.kt")
    ktfmt("0.61").googleStyle().configure {
      it.setMaxWidth(80)
      it.setTrailingCommaManagementStrategy(TrailingCommaManagementStrategy.COMPLETE)
    }
  }
}
