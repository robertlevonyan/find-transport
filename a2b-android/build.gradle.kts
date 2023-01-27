plugins {
  id("com.android.application") version "7.4.0" apply false
  id("com.android.library") version "7.4.0" apply false
  id("com.google.dagger.hilt.android") version "2.44.2" apply false
  id("com.google.gms.google-services") version "4.3.15" apply false
  id("com.google.firebase.crashlytics") version "2.9.2" apply false
  id("com.google.devtools.ksp") version "1.8.0-1.0.9" apply false
  kotlin("android") version "1.8.0" apply false
}

buildscript {
  dependencies {
    classpath("com.android.tools.build:bundletool:0.9.0")
  }
}

tasks.register("clean", Delete::class) {
  delete(rootProject.buildDir)
}
