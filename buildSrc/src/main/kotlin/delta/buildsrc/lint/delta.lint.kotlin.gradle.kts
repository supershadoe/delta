plugins {
    id("com.diffplug.spotless")
}

spotless {
    kotlin {
        target("**/*.kt")
        targetExclude("**/build/**/*.kt")
        ktfmt("0.54").googleStyle().configure {
            it.setMaxWidth(80)
            it.setManageTrailingCommas(true)
        }
    }
}
