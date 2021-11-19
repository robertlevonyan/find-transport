import org.jetbrains.kotlin.konan.properties.Properties

buildscript {
  repositories {
    google()
    jcenter()
    mavenCentral()
  }
  dependencies {
    classpath("com.android.tools.build:gradle:7.0.3")
    classpath("com.google.gms:google-services:4.3.10")
    classpath("com.google.firebase:firebase-crashlytics-gradle:2.8.0")
    classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.6.0")
    classpath("com.android.tools.build:bundletool:0.9.0")
    classpath("com.google.dagger:hilt-android-gradle-plugin:2.38.1")
  }
}

allprojects {
  repositories {
    google()
    jcenter()
    mavenCentral()
    maven { url = uri("https://mapbox.bintray.com/mapbox") }
    maven {
      url = uri("https://api.mapbox.com/downloads/v2/releases/maven")
      authentication { create<BasicAuthentication>("basic") }
      credentials {
        val localProperties = Properties()
        localProperties.load(project.rootProject.file("local.properties").inputStream())

        username = "mapbox"
        password = localProperties.getProperty("MAPBOX_DOWNLOADS_TOKEN")
          ?: throw IllegalArgumentException("SDK Registry key is not specified")
      }
    }
  }
}

tasks.register("clean", Delete::class) {
  delete(rootProject.buildDir)
}
