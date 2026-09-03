plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.compose.compiler)
}

android {
    namespace = "com.helpdesk.app"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.helpdesk.app"
        minSdk = 26
        targetSdk = 34
        versionCode = 2
        versionName = "1.1.0"

        buildConfigField(
            "String",
            "DEFAULT_API_BASE_URL",
            "\"${System.getenv("HELPDESK_API_BASE_URL") ?: "https://help-desk-production-4340.up.railway.app/"}\""
        )

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    signingConfigs {
        create("release") {
            val keystorePath = project.findProperty("KEYSTORE_FILE") as String?
                ?: System.getenv("KEYSTORE_FILE")
            val releaseRequested = gradle.startParameter.taskNames.any {
                it.contains("release", ignoreCase = true)
            }
            if (releaseRequested) {
                require(keystorePath != null && file(keystorePath).exists()) {
                    "Release build requires a signing keystore. Provide KEYSTORE_FILE " +
                        "(project property or env var) pointing to a .jks/.keystore file."
                }
                storeFile = file(keystorePath)
                storePassword = project.findProperty("KEYSTORE_PASSWORD") as String?
                    ?: System.getenv("KEYSTORE_PASSWORD") ?: ""
                keyAlias = project.findProperty("KEY_ALIAS") as String?
                    ?: System.getenv("KEY_ALIAS") ?: ""
                keyPassword = project.findProperty("KEY_PASSWORD") as String?
                    ?: System.getenv("KEY_PASSWORD") ?: ""
                enableV1Signing = true
                enableV2Signing = true
            }
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            signingConfig = signingConfigs.getByName("release")
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.navigation.compose)

    // Jetpack Compose
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.material.icons.extended)
    debugImplementation(libs.androidx.compose.ui.tooling)

    // DI - Koin
    implementation(libs.koin.android)
    implementation(libs.koin.androidx.compose)

    // Networking & Serialization
    implementation(libs.retrofit.core)
    implementation(libs.retrofit.converter.kotlinx)
    implementation(libs.okhttp.core)
    implementation(libs.okhttp.logging)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.kotlinx.coroutines.android)

    // DataStore
    implementation(libs.androidx.datastore.preferences)

    // Image Loading
    implementation(libs.coil.compose)

    // Logging
    implementation(libs.timber)

    // Debug — Memory Leak Detection (debug only)
    debugImplementation(libs.leakcanary.android)

    // Unit Testing
    testImplementation(libs.junit)
    testImplementation(libs.kotlinx.coroutines.test)
}
