import org.jetbrains.kotlin.konan.properties.Properties

plugins {
    id("com.android.application")
    kotlin("android")
    kotlin("kapt")
    kotlin("plugin.serialization") version "1.9.10"
    id("kotlin-parcelize")
    id("com.google.gms.google-services")
    id("com.google.firebase.crashlytics")
    id("com.google.devtools.ksp")
    id("dagger.hilt.android.plugin")
}

android {
    compileSdk = 34
    namespace = "robert.findtransport"
    defaultConfig {
        applicationId = "robert.findtransport"
        minSdk = 23
        targetSdk = 34
        versionCode = 322
        versionName = "4.2.2"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables.useSupportLibrary = true
        multiDexEnabled = true

        ndk {
            abiFilters.addAll(mutableSetOf("armeabi-v7a", "arm64-v8a", "x86", "x86_64"))
            debugSymbolLevel = "SYMBOL_TABLE"
        }

        kapt {
            arguments {
                arg("room.schemaLocation", "$projectDir/schemas")
            }
        }
    }
    buildTypes {
        debug {
            addManifestPlaceholders(mapOf("crashlyticsCollectionEnabled" to false))

            val localProperties = Properties()
            localProperties.load(project.rootProject.file("local.properties").inputStream())

            val mapboxToken = localProperties.getProperty("MAPBOX_TOKEN")
            buildConfigField("String", "MAPBOX_TOKEN", mapboxToken)

            val ipAddress = localProperties.getProperty("IP_ADDRESS")
            buildConfigField("String", "IP_ADDRESS", ipAddress)

            val keyPrefix = localProperties.getProperty("KEY_PREFIX")
            buildConfigField("String", "KEY_PREFIX", keyPrefix)

            val mapboxStyleLight = localProperties.getProperty("MAPBOX_STYLE_LIGHT")
            buildConfigField("String", "MAPBOX_STYLE_LIGHT", mapboxStyleLight)

            val mapboxStyleNight = localProperties.getProperty("MAPBOX_STYLE_NIGHT")
            buildConfigField("String", "MAPBOX_STYLE_NIGHT", mapboxStyleNight)

            applicationIdSuffix = ".debug"
            versionNameSuffix = "-DEBUG"
        }

        release {
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            ndk {
                debugSymbolLevel = "FULL"
            }
            addManifestPlaceholders(mapOf("crashlyticsCollectionEnabled" to true))

            val localProperties = Properties()
            localProperties.load(project.rootProject.file("local.properties").inputStream())

            val mapboxToken = localProperties.getProperty("MAPBOX_TOKEN")
            buildConfigField("String", "MAPBOX_TOKEN", mapboxToken)

            val ipAddress = localProperties.getProperty("IP_ADDRESS")
            buildConfigField("String", "IP_ADDRESS", ipAddress)

            val keyPrefix = localProperties.getProperty("KEY_PREFIX")
            buildConfigField("String", "KEY_PREFIX", keyPrefix)

            val mapboxStyleLight = localProperties.getProperty("MAPBOX_STYLE_LIGHT")
            buildConfigField("String", "MAPBOX_STYLE_LIGHT", mapboxStyleLight)

            val mapboxStyleNight = localProperties.getProperty("MAPBOX_STYLE_NIGHT")
            buildConfigField("String", "MAPBOX_STYLE_NIGHT", mapboxStyleNight)
        }
    }
    lint {
        checkReleaseBuilds = false
        abortOnError = false
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    buildFeatures {
        compose = true
    }
    kotlinOptions {
        jvmTarget = "17"
        freeCompilerArgs = freeCompilerArgs.toMutableList().apply {
            add("-opt-in=kotlin.RequiresOptIn")
            add("-Xcontext-receivers")
        }
    }
    bundle {
        language { enableSplit = false }
        abi { enableSplit = false }
        storeArchive.enable = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.11"
    }
    kapt {
        correctErrorTypes = true
    }
}

dependencies {
    //kotlin
    kotlin("stdlib")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.8.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.8.0")
    implementation("org.jetbrains.kotlin:kotlin-reflect:1.9.23")
    implementation("io.ktor:ktor-client-android:2.3.10")
    implementation("io.ktor:ktor-client-serialization:2.3.10")
    implementation("io.ktor:ktor-client-cio:2.3.10")
    implementation("io.ktor:ktor-client-logging-jvm:2.3.10")
    implementation("io.ktor:ktor-client-content-negotiation:2.3.10")
    implementation("io.ktor:ktor-serialization-kotlinx-json:2.3.10")

    //google
    implementation("com.google.android.material:material:1.11.0")
    implementation("com.google.android.play:core:1.10.3")
    implementation("com.google.android.play:core-ktx:1.8.1")
    implementation("com.google.android.gms:play-services-location:21.2.0")
    implementation("com.google.dagger:hilt-android:2.51.1")
    implementation(platform("com.google.firebase:firebase-bom:32.8.0"))
    releaseImplementation("com.google.firebase:firebase-analytics-ktx")
    releaseImplementation("com.google.firebase:firebase-crashlytics-ktx")

    kapt("com.google.dagger:hilt-android-compiler:2.51.1")

    //androidx
    implementation("androidx.browser:browser:1.8.0")
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.core:core-splashscreen:1.0.1")
    implementation("androidx.multidex:multidex:2.0.1")
    implementation("androidx.room:room-runtime:2.6.1")
    implementation("androidx.room:room-ktx:2.6.1")
    implementation("androidx.room:room-paging:2.6.1")
    implementation("androidx.vectordrawable:vectordrawable:1.1.0")

    ksp("androidx.room:room-compiler:2.6.1")

    //compose
    val composeBom = platform("androidx.compose:compose-bom:2023.10.00")
    implementation("androidx.compose:compose-bom:2024.04.00")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.material3:material3-window-size-class")
    implementation("androidx.compose.foundation:foundation")
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-tooling")

    implementation("com.google.accompanist:accompanist-systemuicontroller:0.34.0")
    implementation("androidx.compose.compiler:compiler:1.5.11")
    implementation("androidx.constraintlayout:constraintlayout-compose:1.0.1")
    implementation("androidx.hilt:hilt-navigation-compose:1.2.0")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.7.0")
    implementation("androidx.navigation:navigation-compose:2.7.7")
    implementation("androidx.paging:paging-compose:3.2.1")
    implementation("io.coil-kt:coil-compose:2.6.0")

    //map
    implementation("com.mapbox.maps:android:11.2.2")
    implementation("com.mapbox.extension:maps-compose:11.2.2")

    //other
    implementation("com.airbnb.android:lottie-compose:6.4.0")
    implementation("com.robertlevonyan.compose:materialchip:3.0.8")
    implementation("me.saket.swipe:swipe:1.3.0")
}
