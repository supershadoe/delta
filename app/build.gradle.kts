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
  compileSdk = 35

  defaultConfig {
    applicationId = "dev.shadoe.delta"
    minSdk = 30
    targetSdk = 35
    versionCode = versionConfig.versionCode
    versionName = versionConfig.versionName

    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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

  buildFeatures { compose = true }

  compileOptions {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
  }

  kotlinOptions { jvmTarget = "21" }
}

dependencies {
  compileOnly(project(path = ":system-api-stubs"))
  implementation(project(path = ":api"))
  implementation(project(path = ":data"))

  implementation(libs.androidx.annotation)
  implementation(libs.androidx.ktx.core)
  implementation(libs.androidx.core.splashscreen)
  implementation(libs.androidx.graphics.shapes)

  platform(libs.compose.bom).let {
    implementation(it)
    androidTestImplementation(it)
  }
  implementation(libs.bundles.compose)

  implementation(libs.hiddenapibypass)
  implementation(libs.hilt.android)

  implementation(libs.kotlinx.coroutines.android)
  implementation(libs.kotlinx.serialization.json)

  ksp(libs.hilt.compiler)
  testImplementation(libs.kotlin.test.junit)
  androidTestImplementation(libs.bundles.instTest)
  debugImplementation(libs.bundles.studioPreview)
}
