plugins {
    id("com.diffplug.spotless")
}

spotless {
    kotlinGradle {
        target("**/*.gradle.kts")
        targetExclude("**/build/**/*.gradle.kts")
        ktfmt("0.54").googleStyle().configure {
            it.setMaxWidth(80)
            it.setManageTrailingCommas(true)
        }
    }
}
