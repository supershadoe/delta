import delta.buildsrc.readSigningConfig

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt)
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
        versionCode = 8
        versionName = "0.2.3"

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

    buildFeatures {
        compose = true
        aidl = false
        renderScript = false
        shaders = false
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }

    kotlinOptions {
        jvmTarget = "21"
    }
}

dependencies {
    val composeBom = platform(libs.androidx.compose.bom)

    compileOnly(project(path = ":system-api-stubs"))
    implementation(project(path = ":hotspot-api"))
    implementation(project(path = ":presentation"))

    implementation(composeBom)
    implementation(libs.bundles.androidx)
    implementation(libs.bundles.compose)
    implementation(libs.compose.ui.tooling.preview)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.hilt.android)
    implementation(libs.material)

    ksp(libs.hilt.compiler)

    testImplementation(libs.junit)

    androidTestImplementation(composeBom)
    androidTestImplementation(libs.bundles.instTest)
    androidTestImplementation(libs.compose.ui.test.junit4)

    debugImplementation(libs.compose.ui.test.manifest)
    debugImplementation(libs.compose.ui.tooling)
}
