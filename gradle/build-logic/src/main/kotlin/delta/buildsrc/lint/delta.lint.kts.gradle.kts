plugins { id("com.diffplug.spotless") }

spotless {
  kotlinGradle {
    target("*.gradle.kts")
    ktfmt("0.56").googleStyle().configure {
      it.setMaxWidth(80)
      it.setManageTrailingCommas(true)
    }
  }
}
