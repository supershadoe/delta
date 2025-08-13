plugins { id("com.diffplug.spotless") }

spotless {
  kotlin {
    target("src/**/*.kt")
    ktfmt("0.56").googleStyle().configure {
      it.setMaxWidth(80)
      it.setManageTrailingCommas(true)
    }
  }
}
