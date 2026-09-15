plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.compose")
    id("org.jetbrains.kotlin.plugin.serialization")
}

android {
    namespace = "com.arz.rates"
    compileSdk = 35

    // FIX: Make Java and Kotlin use the same JVM target.
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

    defaultConfig {
        applicationId = "com.arz.rates"
        minSdk = 26
        targetSdk = 35
        versionCode = 1
        versionName = "1.0.0"
    }

    buildTypes {
        release {
            isMinifyEnabled = false

            buildConfigField(
                "String",
                "NAVASAN_API_KEY",
                "\"freeL8rmnJQvTBwmj6aMsQYTtU1Yg1yP\""
            )
        }

        debug {
            buildConfigField(
                "String",
                "NAVASAN_API_KEY",
                "\"freeL8rmnJQvTBwmj6aMsQYTtU1Yg1yP\""
            )
        }
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    val composeBom = platform(
        "androidx.compose:compose-bom:2025.01.00"
    )

    implementation(composeBom)
    androidTestImplementation(composeBom)

    // Android
    implementation("androidx.core:core-ktx:1.15.0")
    implementation("androidx.activity:activity-compose:1.10.0")

    // Lifecycle
    implementation(
        "androidx.lifecycle:lifecycle-runtime-compose:2.8.7"
    )
    implementation(
        "androidx.lifecycle:lifecycle-viewmodel-compose:2.8.7"
    )

    // Jetpack Compose
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-tooling-preview")
    debugImplementation("androidx.compose.ui:ui-tooling")

    // Material 3
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.material:material-icons-extended")

    // Navigation
    implementation(
        "androidx.navigation:navigation-compose:2.8.5"
    )

    // DataStore
    implementation(
        "androidx.datastore:datastore-preferences:1.1.1"
    )

    // Retrofit
    implementation("com.squareup.retrofit2:retrofit:2.11.0")
    implementation(
        "com.squareup.retrofit2:converter-kotlinx-serialization:2.11.0"
    )

    // OkHttp
    implementation(
        "com.squareup.okhttp3:logging-interceptor:4.12.0"
    )

    // Coroutines
    implementation(
        "org.jetbrains.kotlinx:kotlinx-coroutines-android:1.9.0"
    )

    // Kotlin Serialization
    implementation(
        "org.jetbrains.kotlinx:kotlinx-serialization-json:1.7.3"
    )

    // Android home-screen widget
    implementation("androidx.glance:glance:1.1.1")
    implementation(
        "androidx.glance:glance-appwidget:1.1.1"
    )
    implementation(
        "androidx.glance:glance-material3:1.1.1"
    )

    // Background widget refresh (Android enforces a minimum 15-minute periodic interval)
    implementation("androidx.work:work-runtime-ktx:2.10.0")
}