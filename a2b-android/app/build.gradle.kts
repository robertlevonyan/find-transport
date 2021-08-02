import org.jetbrains.kotlin.konan.properties.Properties

plugins {
  id("com.android.application")
  kotlin("android")
  kotlin("kapt")
  id("kotlin-parcelize")
  id("com.google.gms.google-services")
  id("com.google.firebase.crashlytics")
}

android {
  compileSdk = 31
  defaultConfig {
    applicationId = "robert.findtransport"
    minSdk = 21
    targetSdk = 31
    versionCode = 231
    versionName = "3.6.0"
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
    getByName("debug") {
      addManifestPlaceholders(mapOf("crashlyticsCollectionEnabled" to false))

      val localProperties = Properties()
      localProperties.load(project.rootProject.file("local.properties").inputStream())

      val mapboxToken = localProperties.getProperty("MAPBOX_TOKEN")
      buildConfigField("String", "MAPBOX_TOKEN", mapboxToken)

      val ipAddress = localProperties.getProperty("IP_ADDRESS")
      buildConfigField("String", "IP_ADDRESS", ipAddress)

      val keyPrefix = localProperties.getProperty("KEY_PREFIX")
      buildConfigField("String", "KEY_PREFIX", keyPrefix)
    }

    getByName("release") {
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
    }
  }
  lint {
    isCheckReleaseBuilds = false
    isAbortOnError = false
  }
  compileOptions {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
  }
  buildFeatures {
    viewBinding = true
  }
  kotlinOptions {
    jvmTarget = "1.8"
    languageVersion = "1.4"
  }
  bundle {
    language { enableSplit = false }
    abi { enableSplit = false }
  }
}

dependencies {
  //kotlin
  kotlin("stdlib")
  implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.5.0")
  implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.5.0")

  //google
  implementation("com.google.android.material:material:1.4.0")
  implementation("com.google.android.play:core:1.10.0")
  implementation("com.google.android.play:core-ktx:1.8.1")
  implementation("com.google.android.gms:play-services-location:18.0.0")
  implementation("com.google.code.gson:gson:2.8.7")
  implementation(platform("com.google.firebase:firebase-bom:28.0.1"))
  implementation("com.google.firebase:firebase-analytics-ktx")
  implementation("com.google.firebase:firebase-crashlytics-ktx")

  //androidx
  implementation("androidx.activity:activity-ktx:1.3.0")
  implementation("androidx.appcompat:appcompat:1.3.1")
  implementation("androidx.browser:browser:1.3.0")
  implementation("androidx.cardview:cardview:1.0.0")
  implementation("androidx.constraintlayout:constraintlayout:2.0.4")
  implementation("androidx.core:core-ktx:1.7.0-alpha01")
  implementation("androidx.fragment:fragment-ktx:1.3.6")
  implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.3.1")
  implementation("androidx.lifecycle:lifecycle-common-java8:2.3.1")
  implementation("androidx.lifecycle:lifecycle-extensions:2.2.0")
  implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.3.1")
  implementation("androidx.multidex:multidex:2.0.1")
  implementation("androidx.paging:paging-runtime-ktx:3.0.1")
  implementation("androidx.recyclerview:recyclerview:1.2.1")
  implementation("androidx.room:room-runtime:2.3.0")
  implementation("androidx.room:room-ktx:2.3.0")
  implementation("androidx.vectordrawable:vectordrawable:1.1.0")

  kapt("androidx.room:room-compiler:2.3.0")

  //koin
  implementation("io.insert-koin:koin-core:3.1.0")
  implementation("io.insert-koin:koin-android:3.1.0")

  //squareup
  implementation("com.squareup.retrofit2:retrofit:2.9.0")
  implementation("com.squareup.retrofit2:converter-gson:2.9.0")
  implementation("com.squareup.okhttp3:logging-interceptor:4.9.1")

  //mapbox
  implementation("com.mapbox.maps:android:10.0.0-rc.4")
  implementation("com.mapbox.navigation:android:2.0.0-beta.20")
  implementation("com.mapbox.mapboxsdk:mapbox-android-plugin-annotation-v9:0.9.0")

  //other
  implementation("com.airbnb.android:lottie:3.4.2")
  implementation("com.github.terrakok:cicerone:7.0")
  implementation("com.github.ybq:Android-SpinKit:1.4.0")
  implementation("com.jakewharton.retrofit:retrofit2-kotlinx-serialization-converter:0.8.0")
  implementation("net.yslibrary.keyboardvisibilityevent:keyboardvisibilityevent:2.3.0")
}
