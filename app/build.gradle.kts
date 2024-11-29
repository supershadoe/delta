plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.kotlin.android)
}

android {
    namespace = "dev.shadoe.delta"
    compileSdk = 35

    defaultConfig {
        applicationId = "dev.shadoe.delta"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    buildFeatures {
        compose = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }
}

dependencies {
    val composeBom = platform(libs.androidx.compose.bom)
    implementation(composeBom)
    implementation(libs.bundles.androidx)
    implementation(libs.bundles.compose)
    implementation(libs.material)
    implementation(libs.shizuku)
    implementation(libs.compose.ui.tooling.preview)

    testImplementation(libs.junit)
    androidTestImplementation(composeBom)
    androidTestImplementation(libs.bundles.instTest)
    androidTestImplementation(libs.compose.ui.test.junit4)
    debugImplementation(libs.compose.ui.tooling)
    debugImplementation(libs.compose.ui.test.manifest)
}