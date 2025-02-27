plugins {
    id("com.diffplug.spotless")
}

spotless {
    kotlinGradle {
        target("**/*.gradle.kts")
        targetExclude("**/build/**/*.gradle.kts")
        ktlint("1.5.0")
    }
}
