import com.android.build.api.dsl.LibraryExtension
import com.android.build.gradle.tasks.AidlCompile

plugins {
  alias(libs.plugins.android.library)
  id("delta.lint.java")
  id("delta.lint.kts")
}

configure<LibraryExtension> {
  namespace = "dev.shadoe.systemapistubs"
  compileSdk = 36
  enableKotlin = false

  defaultConfig {
    minSdk = 30
    consumerProguardFiles("consumer-rules.pro")
  }

  buildTypes {
    release {
      isMinifyEnabled = false
      proguardFiles(
        getDefaultProguardFile("proguard-android-optimize.txt"),
        "proguard-rules.pro",
      )
    }
  }

  buildFeatures { aidl = true }

  // TODO check if we can switch this with a gradle task where we compile
  //   using aidl ourselves (using androidComponents.sdkComponents.aidl)
  afterEvaluate {
    tasks.withType<AidlCompile>().configureEach {
      doLast {
        outputs.files.forEach { fileOrFolder ->
          fileOrFolder.walkTopDown().forEach { file ->
            file
              .takeIf { file.extension == "java" }
              ?.apply {
                val sanitized = readText().replace("\\", "\\\\")
                writeText(sanitized)
              }
          }
        }
      }
    }
  }
}

java { toolchain { languageVersion = JavaLanguageVersion.of(21) } }

dependencies {
  implementation(libs.androidx.annotation)
  annotationProcessor(libs.refine.processor)
  implementation(libs.refine.annotation)
}
