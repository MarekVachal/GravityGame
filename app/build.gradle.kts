    plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.serialization") version "2.3.0"
    id("org.jetbrains.kotlin.plugin.compose") version "2.3.0"
    id("com.google.devtools.ksp")
    id("com.google.gms.google-services")
    id("io.sentry.android.gradle") version "5.12.2"
    id("com.google.dagger.hilt.android")
}

android {
    namespace = "com.marks2games.gravitygame"
    compileSdk = 35
    ndkVersion = "28.0.13004108"

    signingConfigs {
        create("release") {
            storeFile = file("/home/marek/Games/GravityGame/KeyStore/Key.jks")
            storePassword = project.findProperty("storePassword") as String
            keyAlias = project.findProperty("keyAlias") as String
            keyPassword = project.findProperty("keyPassword") as String
            storeType = "PKCS12"
        }
    }

    defaultConfig {
        applicationId = "com.marks2games.gravitygame"
        minSdk = 26
        targetSdk = 35
        versionCode = 11
        versionName = "1.2.8"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        debug{
            isMinifyEnabled = false
            isShrinkResources = false
            isDebuggable = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
        release{
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("release")
            ndk {
                debugSymbolLevel = "FULL"
            }
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlin {
        compilerOptions {
            jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17)
            freeCompilerArgs.add("-Xjvm-default=all")
        }
    }
    buildFeatures {
        compose = true
    }

    packaging {
        resources {
            excludes += "/META-INF/*"
        }
    }
    bundle {
        language{
            enableSplit = false
        }
    }
    tasks {
        withType<JavaCompile> {
            options.compilerArgs.add("-Xlint:deprecation")
        }
    }
}

sentry {
    includeProguardMapping = true
    autoUploadProguardMapping = true
    includeDependenciesReport = true
}

dependencies {

    val composeBomVersion = "2025.12.01"
    val roomVersion = "2.8.4"
    val credentialVersion = "1.5.0"
    val hiltVersion = "2.57.2"
    val ktorVersion = "3.3.3"
    val coroutineVersion = "1.10.2"
    val sentryVersion = "8.29.0"

    //Ktor
    implementation ("io.ktor:ktor-client-core:$ktorVersion")
    implementation ("io.ktor:ktor-client-cio:$ktorVersion")
    implementation ("io.ktor:ktor-client-content-negotiation:$ktorVersion")
    implementation ("io.ktor:ktor-serialization-kotlinx-json:$ktorVersion")

    //Hilt
    implementation ("com.google.dagger:hilt-android:$hiltVersion")
    ksp ("com.google.dagger:hilt-compiler:$hiltVersion")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.10.0") //Not sure I need it

    //Coin. For getting image from URL
    implementation("io.coil-kt:coil-compose:2.7.0")

    //Credential manager
    implementation ("androidx.credentials:credentials:$credentialVersion")
    implementation ("androidx.credentials:credentials-play-services-auth:$credentialVersion")
    implementation ("com.google.android.libraries.identity.googleid:googleid:1.1.1")

    //Sentry
    implementation ("io.sentry:sentry-android:$sentryVersion")
    implementation("io.sentry:sentry-compose-android:$sentryVersion")
    implementation("io.sentry:sentry-android-navigation:$sentryVersion")
    implementation("io.sentry:sentry-kotlin-extensions:$sentryVersion")

    //Play Games Services
    //implementation ("com.google.android.gms:play-services-games-v2:20.1.2")
    implementation("com.google.android.gms:play-services-auth:21.4.0")
    //implementation ("com.google.android.gms:play-services-base:18.5.0")
    //implementation ("com.google.android.gms:play-services-tasks:18.2.0")

    //Firebase
    implementation(platform("com.google.firebase:firebase-bom:34.7.0"))
    implementation("com.google.firebase:firebase-common")
    implementation("com.google.firebase:firebase-analytics")
    implementation("com.google.firebase:firebase-auth")
    implementation("com.google.firebase:firebase-database")
    implementation ("com.google.firebase:firebase-firestore")
    implementation ("com.google.firebase:firebase-messaging")
    implementation("com.google.firebase:firebase-inappmessaging")
    implementation("com.google.auth:google-auth-library-oauth2-http:1.41.0")

    //gRPC (firestore used it for network operation
    implementation("io.grpc:grpc-okhttp:1.78.0")

    //Compose navigation
    implementation("androidx.navigation:navigation-compose:2.9.6")

    //Coroutines
    implementation ("org.jetbrains.kotlinx:kotlinx-coroutines-core:$coroutineVersion")
    implementation ("org.jetbrains.kotlinx:kotlinx-coroutines-android:$coroutineVersion")
    implementation ("org.jetbrains.kotlinx:kotlinx-coroutines-play-services:$coroutineVersion")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.10.0")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.10.0")

    //Core
    implementation("androidx.core:core-ktx:1.17.0")
    implementation("androidx.activity:activity-compose:1.12.2")
    implementation(platform("androidx.compose:compose-bom:$composeBomVersion"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.appcompat:appcompat:1.7.1")
    implementation("com.google.android.material:material:1.13.0")

    //Constraint layout
    implementation("androidx.constraintlayout:constraintlayout:2.2.1")
    implementation ("androidx.constraintlayout:constraintlayout-compose:1.1.1")

    //Room database
    implementation("androidx.room:room-ktx:$roomVersion")
    implementation("androidx.room:room-runtime: $roomVersion")
    ksp("androidx.room:room-compiler:$roomVersion")

    //Testing
    testImplementation("junit:junit:4.13.2")
    testImplementation("org.mockito:mockito-core:5.21.0")
    testImplementation("org.mockito.kotlin:mockito-kotlin:6.1.0")
    testImplementation("io.mockk:mockk:1.14.7")
    androidTestImplementation("androidx.test.ext:junit:1.3.0")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.7.0")
    androidTestImplementation(platform("androidx.compose:compose-bom:$composeBomVersion"))
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")
    testImplementation("com.google.dagger:hilt-android-testing:$hiltVersion")
    kspTest("com.google.dagger:hilt-android-compiler:$hiltVersion")
    androidTestImplementation("com.google.dagger:hilt-android-testing:$hiltVersion")
    kspAndroidTest("com.google.dagger:hilt-android-compiler:$hiltVersion")
}

ksp {
    arg("room.schemaLocation", "$projectDir/schemas")
    arg("room.incremental", "true")
}

hilt {
    enableAggregatingTask = true
}