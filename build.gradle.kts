// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.compose.compiler) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.serialization) apply false
    alias(libs.plugins.refine) apply false
    alias(libs.plugins.spotless)
}

spotless {
    format("misc") {
        target("*.gradle", ".gitattributes", ".gitignore", ".txt")
        trimTrailingWhitespace()
        leadingTabsToSpaces()
        endWithNewline()
    }
    flexmark {
        target("**/*.md")
        flexmark()
    }
    java {
        importOrder()
        removeUnusedImports()
        googleJavaFormat().aosp().reflowLongStrings()
        formatAnnotations()
        target("**/*.java")
        targetExclude("build/**")
    }
    kotlin {
        target("**/*.kt", "**/*.kts")
        targetExclude("**/build/**/*.kt")
        ktlint("1.5.0")
            .customRuleSets(
                listOf(
                    "io.nlopez.compose.rules:ktlint:0.4.22",
                ),
            )
        trimTrailingWhitespace()
        endWithNewline()
    }
    kotlinGradle {
        target("*.gradle.kts")
        ktlint("1.5.0")
        trimTrailingWhitespace()
        endWithNewline()
    }
}
