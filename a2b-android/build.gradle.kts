plugins {
  id("com.android.application") version "7.3.1" apply false
  id("com.android.library") version "7.3.1" apply false
  id("com.google.dagger.hilt.android") version "2.41" apply false
  id("com.google.gms.google-services") version "4.3.14" apply false
  id("com.google.firebase.crashlytics") version "2.9.0" apply false
  id("org.jetbrains.kotlin.android") version "1.6.21" apply false
}

buildscript {
  dependencies {
    classpath("com.android.tools.build:bundletool:0.9.0")
  }
}

tasks.register("clean", Delete::class) {
  delete(rootProject.buildDir)
}
