plugins {
    id("com.diffplug.spotless")
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
    }
    kotlinGradle {
        target("*.gradle.kts")
        ktlint("1.5.0")
    }
}
