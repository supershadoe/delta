import com.android.build.gradle.tasks.AidlCompile

plugins {
    alias(libs.plugins.android.library)
}

android {
    namespace = "dev.shadoe.systemapistubs"
    compileSdk = 35

    defaultConfig {
        minSdk = 30
        consumerProguardFiles("consumer-rules.pro")
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
        aidl = true
        renderScript = false
        shaders = false
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }

    afterEvaluate {
        tasks.withType<AidlCompile>().configureEach {
            doLast {
                outputs.files.forEach { fileOrFolder ->
                    fileOrFolder.walkTopDown().forEach { file ->
                        file.takeIf { file.extension == "java" }?.apply {
                            val sanitized = readText().replace("\\", "\\\\")
                            writeText(sanitized)
                        }
                    }
                }
            }
        }
    }
}

dependencies {
    implementation(libs.androidx.annotation)
}