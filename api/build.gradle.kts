plugins {
  kotlin("jvm")
  id("delta.lint.kotlin")
  id("delta.lint.kts")
}

kotlin { jvmToolchain(21) }

dependencies { implementation(libs.androidx.annotation) }
