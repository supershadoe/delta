plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "dev.shadoe.delta"
    compileSdk = 35
    compileSdkPreview = "Baklava"

    defaultConfig {
        applicationId = "dev.shadoe.delta"
        minSdk = 30
        targetSdk = 35
        versionCode = 6
        versionName = "0.2.1"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
                "proguard-rules.pro"
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

    implementation(project(path = ":hotspot-api"))

    implementation(composeBom)
    implementation(libs.bundles.androidx)
    implementation(libs.bundles.compose)
    implementation(libs.bundles.shizuku)
    implementation(libs.compose.ui.tooling.preview)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.material)

    testImplementation(libs.junit)

    androidTestImplementation(composeBom)
    androidTestImplementation(libs.bundles.instTest)
    androidTestImplementation(libs.compose.ui.test.junit4)

    debugImplementation(libs.compose.ui.test.manifest)
    debugImplementation(libs.compose.ui.tooling)
}