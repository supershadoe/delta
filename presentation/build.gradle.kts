plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    id("delta.lint.kotlin")
    id("delta.lint.kts")
}

android {
    namespace = "dev.shadoe.delta.presentation"
    compileSdk = 35

    defaultConfig {
        minSdk = 30

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
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
    compileOnly(project(path = ":system-api-stubs"))
    implementation(libs.hiddenapibypass)
    implementation(libs.refine.runtime)
    implementation(libs.bundles.androidx)
    implementation(libs.bundles.shizuku)
}
