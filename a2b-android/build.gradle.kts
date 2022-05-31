buildscript {
  repositories {
    google()
    jcenter()
    mavenCentral()
  }
  dependencies {
    classpath("com.android.tools.build:gradle:7.1.3")
    classpath("com.google.gms:google-services:4.3.10")
    classpath("com.google.firebase:firebase-crashlytics-gradle:2.8.1")
    classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.6.21")
    classpath("com.android.tools.build:bundletool:0.9.0")
    classpath("com.google.dagger:hilt-android-gradle-plugin:2.41")
  }
}

tasks.register("clean", Delete::class) {
  delete(rootProject.buildDir)
}
