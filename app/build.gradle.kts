import delta.buildsrc.readSigningConfig
import delta.buildsrc.versionConfig

plugins {
  alias(libs.plugins.android.application)
  alias(libs.plugins.compose.compiler)
  alias(libs.plugins.kotlin.android)
  alias(libs.plugins.kotlin.serialization)
  alias(libs.plugins.ksp)
  alias(libs.plugins.hilt)
  id("delta.app.version")
  id("delta.lint.kotlin")
  id("delta.lint.kts")
}

android {
  namespace = "dev.shadoe.delta"
  compileSdk = 36

  defaultConfig {
    applicationId = "dev.shadoe.delta"
    minSdk = 30
    targetSdk = 36
    versionCode = versionConfig.versionCode
    versionName = versionConfig.versionName

    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
  }

  // Remove dependency blob signed with Google's public key
  // https://android.izzysoft.de/articles/named/iod-scan-apkchecks#blobs
  // Not sure if this causes any issues with Play Protect, but transparent
  // reproducible builds > Google's trust in this app.
  dependenciesInfo {
    includeInApk = false
    includeInBundle = false
  }

  signingConfigs {
    create("default") {
      readSigningConfig(file("sign.json"))?.let { c ->
        keyAlias = c.keyAlias
        keyPassword = c.keyPassword
        storeFile = file(c.storeFile)
        storePassword = c.storePassword
      }
    }
  }

  flavorDimensions += "default"

  productFlavors {
    create("debugKeySigned") {
      dimension = "default"
      signingConfig = signingConfigs.getByName("debug")
    }
    create("defaultKeySigned") {
      dimension = "default"
      signingConfig = signingConfigs.getByName("default")
    }
  }

  buildTypes {
    debug {
      applicationIdSuffix = ".debug"
      isMinifyEnabled = false
      isShrinkResources = false
    }

    release {
      isMinifyEnabled = true
      isShrinkResources = true
      proguardFiles(
        getDefaultProguardFile("proguard-android-optimize.txt"),
        "proguard-rules.pro",
      )
    }
  }

  androidResources {
    @Suppress("UnstableApiUsage")
    generateLocaleConfig = true
  }

  buildFeatures { compose = true }

  java { toolchain { languageVersion = JavaLanguageVersion.of(21) } }

  packaging {
    resources {
      excludes += "META-INF/LICENSE.md"
      excludes += "META-INF/LICENSE-notice.md"
    }
  }
}

dependencies {
  compileOnly(project(path = ":system-api-stubs"))
  implementation(project(path = ":api"))
  implementation(project(path = ":data"))

  implementation(libs.androidx.annotation)
  implementation(libs.androidx.ktx.core)
  implementation(libs.androidx.graphics.shapes)

  platform(libs.compose.bom).let {
    implementation(it)
    androidTestImplementation(it)
  }
  implementation(libs.bundles.compose)

  implementation(libs.hiddenapibypass)
  implementation(libs.hilt.android)

  implementation(libs.kotlinx.coroutines.android)
  implementation(libs.kotlinx.datetime)
  implementation(libs.kotlinx.serialization.json)

  ksp(libs.hilt.compiler)
  androidTestImplementation(libs.kotlin.test.junit)
  androidTestImplementation(libs.mockk)
  androidTestImplementation(libs.kotlinx.coroutines.test)
  androidTestImplementation(libs.bundles.instTest)
  debugImplementation(libs.bundles.studioPreview)
  debugImplementation(libs.leakcanary)
}
