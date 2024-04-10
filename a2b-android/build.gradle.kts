plugins {
  id("com.android.application") version "8.3.1" apply false
  id("com.android.library") version "8.3.1" apply false
  id("com.google.dagger.hilt.android") version "2.51" apply false
  id("com.google.gms.google-services") version "4.3.15" apply false
  id("com.google.firebase.crashlytics") version "2.9.5" apply false
  id("com.google.devtools.ksp") version "1.9.23-1.0.20" apply false
  kotlin("android") version "1.9.23" apply false
  kotlin("kapt") version "1.9.23" apply false
}

buildscript {
  dependencies {
    classpath("com.android.tools.build:bundletool:0.9.0")
  }
}

tasks.register("clean", Delete::class) {
  delete(layout.buildDirectory)
}
