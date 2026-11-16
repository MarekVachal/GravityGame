// Top-level build file where you can add configuration options common to all subprojects/modules.
plugins {
    id("com.android.application") version "9.5.1" apply false
    //id("org.jetbrains.kotlin.android") version "2.3.10" apply false
    id("com.google.devtools.ksp") version "2.3.6" apply false
    id("androidx.room") version "2.8.5" apply false
    id("com.google.gms.google-services") version "4.6.0" apply false
    id("com.google.dagger.hilt.android") version "2.60.1" apply false
}

buildscript {
    dependencies {
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:2.3.20")
        classpath("com.google.devtools.ksp:symbol-processing-gradle-plugin:2.3.6")
    }
}