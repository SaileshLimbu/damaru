plugins {
    id("com.android.library")
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt)
    id("kotlin-kapt")
    kotlin("plugin.serialization") version "2.1.0"
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

    buildFeatures {
        viewBinding = true
        buildConfig = true
    }
}

dependencies {
    api(libs.hilt.android)
    ksp(libs.hilt.compiler)

    implementation(libs.webrtc)
    implementation(libs.websocket)
    implementation(libs.gson)
    implementation(libs.security.crypto)
    api(libs.retrofit)
    api(libs.converter.gson)
    api(libs.logging.interceptor)


    api(libs.androidx.core.ktx)
    api(libs.androidx.appcompat)
    api(libs.material)
    api(libs.androidx.activity)
    api(libs.androidx.constraintlayout)
    api(libs.androidx.navigation.fragment.ktx)
    api(libs.androidx.navigation.ui.ktx)

    api(libs.lifecycle.viewmodel)
    api(libs.lifecycle.livedata)
    api(libs.activity.ktx)
    api(libs.fragment.ktx)

    api(libs.hilt.android)
    ksp(libs.hilt.compiler)

    api(libs.webrtc)
    api(libs.websocket)
    api(libs.gson)
    api(libs.security.crypto)

    api(libs.coroutine.core)
    api(libs.coroutine.android)

    api(libs.lottie)
    api(libs.kotlinx.serialization.json)

    api(libs.androidx.swiperefreshlayout)
    api (libs.glide)
}