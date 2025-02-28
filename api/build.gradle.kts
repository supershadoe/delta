plugins {
  alias(libs.plugins.android.library)
  alias(libs.plugins.kotlin.android)
  alias(libs.plugins.refine)
  id("delta.lint.kotlin")
  id("delta.lint.kts")
}

android {
  namespace = "dev.shadoe.delta.api"
  compileSdk = 35

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

  compileOptions {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
  }

  kotlinOptions { jvmTarget = "21" }
}

dependencies {
  compileOnly(project(path = ":system-api-stubs"))
  implementation(libs.androidx.annotation)
}
