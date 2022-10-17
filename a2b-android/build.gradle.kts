buildscript {
  repositories {
    google()
    jcenter()
    mavenCentral()
  }
  dependencies {
    classpath("com.android.tools.build:gradle:7.3.1")
    classpath("com.google.gms:google-services:4.3.14")
    classpath("com.google.firebase:firebase-crashlytics-gradle:2.9.2")
    classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.6.20")
    classpath("com.android.tools.build:bundletool:0.9.0")
    classpath("com.google.dagger:hilt-android-gradle-plugin:2.41")
  }
}

tasks.register("clean", Delete::class) {
  delete(rootProject.buildDir)
}
