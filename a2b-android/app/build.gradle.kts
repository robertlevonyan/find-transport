import org.jetbrains.kotlin.konan.properties.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.kotlin.parcelize)
    alias(libs.plugins.ksp)
    alias(libs.plugins.gms)
    alias(libs.plugins.firebase.crashlytics)
    alias(libs.plugins.hilt)
}

android {
    compileSdk = 34
    namespace = "robert.findtransport"
    defaultConfig {
        applicationId = "robert.findtransport"
        minSdk = 23
        targetSdk = 34
        versionCode = 324
        versionName = "4.2.4"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables.useSupportLibrary = true
        multiDexEnabled = true

        ndk {
            abiFilters.addAll(mutableSetOf("armeabi-v7a", "arm64-v8a", "x86", "x86_64"))
            debugSymbolLevel = "SYMBOL_TABLE"
        }

        ksp {
            arguments.toMutableMap()["room.schemaLocation"] = "$projectDir/schemas"
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
}

dependencies {
    //kotlin
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.kotlin.reflect)
    implementation(libs.ktor.client.android)
    implementation(libs.ktor.client.serialization)
    implementation(libs.ktor.client.cio)
    implementation(libs.ktor.client.logging.jvm)
    implementation(libs.ktor.client.content.negotiation)
    implementation(libs.ktor.serialization.kotlinx.json)

    //google
    implementation(libs.material)
    implementation(libs.play.core)
    implementation(libs.play.core.ktx)
    implementation(libs.play.services.location)
    implementation(libs.hilt.android)
    implementation(platform(libs.firebase.bom))
    implementation(libs.bundles.firebase)

    ksp(libs.hilt.android.compiler)

    //androidx
    implementation(libs.browser)
    implementation(libs.core.ktx)
    implementation(libs.core.splashscreen)
    implementation(libs.multidex)
    implementation(libs.room.runtime)
    implementation(libs.room.ktx)
    implementation(libs.room.paging)
    implementation(libs.vectordrawable)

    ksp(libs.room.compiler)

    //compose
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.bundles.compose)

    implementation(libs.accompanist.systemuicontroller)
    implementation(libs.activity.compose)
    implementation(libs.compose.compiler)
    implementation(libs.compose.navigation)
    implementation(libs.compose.paging)
    implementation(libs.compose.coil)
    implementation(libs.constraintlayout.compose)
    implementation(libs.hilt.navigation.compose)
    implementation(libs.lifecycle.viewmodel.compose)

    //map
    implementation(libs.mapbox.android)
    implementation(libs.mapbox.android.compose)

    //other
    implementation(libs.compose.lottie)
    implementation(libs.materialchip)
    implementation(libs.swipe)
}
