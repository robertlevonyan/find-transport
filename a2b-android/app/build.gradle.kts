import org.jetbrains.kotlin.konan.properties.Properties

plugins {
  id("com.android.application")
  kotlin("android")
  kotlin("kapt")
  id("kotlin-parcelize")
  id("com.google.gms.google-services")
  id("com.google.firebase.crashlytics")
  id("dagger.hilt.android.plugin")
}

android {
  compileSdk = 33
  defaultConfig {
    applicationId = "robert.findtransport"
    minSdk = 23
    targetSdk = 33
    versionCode = 281
    versionName = "3.8.0ß5"
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
      proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "$project.rootDir/tools/proguard-rules.pro")
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
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
  }
  buildFeatures {
    viewBinding = true
    compose = true
  }
  kotlinOptions {
    jvmTarget = "11"
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
    kotlinCompilerExtensionVersion = "1.2.0-rc01"
  }
  kapt {
    correctErrorTypes = true
  }
}

dependencies {
  //kotlin
  kotlin("stdlib")
  implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4")
  implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.6.4")

  //google
  implementation("com.google.android.material:material:1.8.0-alpha03")
  implementation("com.google.android.play:core:1.10.3")
  implementation("com.google.android.play:core-ktx:1.8.1")
  implementation("com.google.android.gms:play-services-location:20.0.0")
  implementation("com.google.code.gson:gson:2.10")
  implementation("com.google.dagger:hilt-android:2.44.2")
  releaseImplementation(platform("com.google.firebase:firebase-bom:31.1.0"))
  releaseImplementation("com.google.firebase:firebase-analytics-ktx")
  releaseImplementation("com.google.firebase:firebase-crashlytics-ktx")

  kapt("com.google.dagger:hilt-android-compiler:2.44.2")

  //androidx
  implementation("androidx.activity:activity-ktx:1.6.1")
  implementation("androidx.appcompat:appcompat:1.5.1")
  implementation("androidx.browser:browser:1.4.0")
  implementation("androidx.cardview:cardview:1.0.0")
  implementation("androidx.constraintlayout:constraintlayout:2.1.4")
  implementation("androidx.core:core-ktx:1.9.0")
  implementation("androidx.core:core-splashscreen:1.0.0")
  implementation("androidx.fragment:fragment-ktx:1.5.4")
  implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.5.1")
  implementation("androidx.lifecycle:lifecycle-common-java8:2.5.1")
  implementation("androidx.lifecycle:lifecycle-extensions:2.2.0")
  implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.5.1")
  implementation("androidx.multidex:multidex:2.0.1")
  implementation("androidx.paging:paging-runtime-ktx:3.1.1")
  implementation("androidx.recyclerview:recyclerview:1.2.1")
  implementation("androidx.room:room-runtime:2.4.3")
  implementation("androidx.room:room-ktx:2.4.3")
  implementation("androidx.room:room-paging:2.4.3")
  implementation("androidx.vectordrawable:vectordrawable:1.1.0")

  kapt("androidx.room:room-compiler:2.4.3")

  //compose
  implementation("com.google.accompanist:accompanist-systemuicontroller:0.28.0")
  implementation("androidx.activity:activity-compose:1.7.0-alpha02")
  implementation("androidx.constraintlayout:constraintlayout-compose:1.0.1")
  implementation("androidx.hilt:hilt-navigation-compose:1.0.0")
  implementation("androidx.navigation:navigation-compose:2.5.3")
  implementation("androidx.paging:paging-compose:1.0.0-alpha17")
  implementation("androidx.compose.compiler:compiler:1.3.2")
  implementation("androidx.compose.ui:ui:1.4.0-alpha02")
  implementation("androidx.compose.material3:material3:1.1.0-alpha02")
  implementation("androidx.compose.material3:material3-window-size-class:1.1.0-alpha02")
  implementation("androidx.compose.ui:ui-tooling:1.4.0-alpha02")
  implementation("io.coil-kt:coil-compose:2.2.2")

  //squareup
  implementation("com.squareup.retrofit2:retrofit:2.9.0")
  implementation("com.squareup.retrofit2:converter-gson:2.9.0")
  implementation("com.squareup.okhttp3:logging-interceptor:5.0.0-alpha.3")

  //mapbox
  implementation("com.mapbox.maps:android:10.9.1")
  implementation("com.mapbox.navigation:android:2.4.0")

  //other
  implementation("com.airbnb.android:lottie-compose:5.2.0")
  implementation("com.jakewharton.retrofit:retrofit2-kotlinx-serialization-converter:0.8.0")
  implementation("net.yslibrary.keyboardvisibilityevent:keyboardvisibilityevent:2.3.0")
}
