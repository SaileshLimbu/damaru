plugins {
    id("com.android.library")
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt)
    id("kotlin-kapt")
}

android {
    namespace = "com.powersoft.common"
    compileSdk = 35

    defaultConfig {
        minSdk = 24
        targetSdk = 35
        ndkVersion = "28.0.12674087"

        multiDexEnabled = true
        externalNativeBuild {
            cmake {
                cppFlags("")
            }
        }
        ndk {
            abiFilters.addAll(listOf("armeabi-v7a", "x86", "x86_64", "arm64-v8a"))
        }
    }

    externalNativeBuild {
        cmake {
            path = file("src/main/cpp/CMakeLists.txt")
            version = "3.31.1"
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
}

dependencies {
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)

    implementation(libs.webrtc)
    implementation(libs.websocket)
    implementation(libs.gson)
    implementation(libs.security.crypto)
    api(libs.retrofit)
    api(libs.converter.gson)
    api(libs.logging.interceptor)
}