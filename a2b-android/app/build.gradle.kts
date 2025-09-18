import com.android.build.api.variant.BuildConfigField
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
    alias(libs.plugins.compose.compiler)
}

android {
    compileSdk = 36
    namespace = "robert.findtransport"
    defaultConfig {
        applicationId = "robert.findtransport"
        minSdk = 26
        targetSdk = 36
        versionCode = 341
        versionName = "4.4.1"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables.useSupportLibrary = true
        multiDexEnabled = true

        ndk {
            abiFilters.addAll(setOf("armeabi-v7a", "arm64-v8a", "x86_64"))
            debugSymbolLevel = "SYMBOL_TABLE"
        }

        ksp.arg("room.schemaLocation", "$projectDir/schemas")
        externalNativeBuild {
            cmake {
                arguments += listOf("-DANDROID_SUPPORT_FLEXIBLE_PAGE_SIZES=ON")
            }
        }
    }
    ndkVersion = "29.0.14033849"
    buildTypes {
        debug {
            applicationIdSuffix = ".debug"
            versionNameSuffix = "-DEBUG"
            with(manifestPlaceholders) {
                set("crashlyticsCollectionEnabled", false)
            }
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
            with(manifestPlaceholders) {
                set("crashlyticsCollectionEnabled", false)
            }
        }
    }
    lint {
        checkReleaseBuilds = false
        abortOnError = false
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
    bundle {
        language { enableSplit = false }
        abi { enableSplit = false }
        storeArchive.enable = true
    }
}

kotlin {
    jvmToolchain(17)
    sourceSets.all {
        languageSettings.enableLanguageFeature("ExplicitBackingFields")
    }
    compilerOptions {
        freeCompilerArgs.addAll(
            "-opt-in=kotlin.RequiresOptIn",
            "-Xcontext-receivers"
        )
    }
}

androidComponents {
    onVariants {
        val localProperties = Properties()
        localProperties.load(project.rootProject.file("local.properties").inputStream())

        val mapboxToken = localProperties.getProperty("MAPBOX_TOKEN")
        val ipAddress = localProperties.getProperty("IP_ADDRESS")
        val keyPrefix = localProperties.getProperty("KEY_PREFIX")
        val mapboxStyleLight = localProperties.getProperty("MAPBOX_STYLE_LIGHT")
        val mapboxStyleNight = localProperties.getProperty("MAPBOX_STYLE_NIGHT")

        it?.buildConfigFields?.run {
            put("MAPBOX_TOKEN", BuildConfigField("String", mapboxToken, "MAPBOX_TOKEN"))
            put("IP_ADDRESS", BuildConfigField("String", ipAddress, "IP_ADDRESS"))
            put("KEY_PREFIX", BuildConfigField("String", keyPrefix, "KEY_PREFIX"))
            put(
                "MAPBOX_STYLE_LIGHT",
                BuildConfigField("String", mapboxStyleLight, "MAPBOX_STYLE_LIGHT")
            )
            put(
                "MAPBOX_STYLE_NIGHT",
                BuildConfigField("String", mapboxStyleNight, "MAPBOX_STYLE_NIGHT")
            )
        }
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
    implementation(libs.play.review)
    implementation(libs.play.app.update)
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
    implementation(libs.vectordrawable)

    ksp(libs.room.compiler)

    //compose
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.bundles.compose)

    implementation(libs.activity.compose)
    implementation(libs.compose.navigation)
    implementation(libs.compose.coil)
    implementation(libs.constraintlayout.compose)
    implementation(libs.hilt.navigation.compose)
    implementation(libs.lifecycle.viewmodel.compose)

    //map
    implementation(libs.bundles.mapbox)

    //other
    implementation(libs.compose.lottie)
    implementation(libs.materialchip)
    implementation(libs.swipe)
}
