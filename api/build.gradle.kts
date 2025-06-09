plugins {
  kotlin("jvm")
  id("delta.lint.kotlin")
  id("delta.lint.kts")
}

java { toolchain { languageVersion = JavaLanguageVersion.of(21) } }

dependencies { implementation(libs.androidx.annotation) }
