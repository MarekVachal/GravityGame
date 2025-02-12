# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile
#General
-keepattributes Signature

#Sentry
-keep class io.sentry.** { *; }
-dontwarn io.sentry.**

#Google Play Services
-keep class com.google.android.gms.games.** { *; }
-dontwarn com.google.android.gms.games.**

#Credential manager
-if class androidx.credentials.CredentialManager
-keep class androidx.credentials.playservices.** {
  *;
}
-keep class androidx.credentials.** { *; }
-dontwarn androidx.credentials.**

#Firebase
-keep class com.google.firebase.auth.** { *; }
-keep class com.google.android.gms.auth.api.signin.** { *; }
-keep class com.google.android.gms.auth.api.Auth { *; }
-keep class com.google.firebase.** { *; }
-keep class com.google.firebase.firestore.** { *; }
-keep class com.google.android.gms.** { *; }
-keep class com.google.firebase.database.** { *; }
-dontwarn com.google.firebase.**
-keep class com.google.firebase.messaging.** { *; }
-dontwarn com.google.firebase.messaging.**
-keepclassmembers class com.marks2games.gravitygame.firebase.** {
    public <init>();
    public *;
}
-keepnames class com.marks2games.gravitygame.firebase.** { *; }

#gRPC
-keep class io.grpc.** { *; }
-dontwarn io.grpc.**

#Jetpack Compose and Navigation
-keep class androidx.compose.** { *; }
-dontwarn androidx.compose.**
-keep class androidx.navigation.** { *; }
-dontwarn androidx.navigation.**

#Room
-keep class androidx.room.** { *; }
-dontwarn androidx.room.**
-keep @androidx.room.Dao class * { *; }
-keep @androidx.room.Entity class * { *; }
-keep @androidx.room.Database class * { *; }
-keep @androidx.room.Query class * { *; }
-keep @androidx.room.Insert class * { *; }
-keep @androidx.room.Delete class * { *; }

#AndroidX Core, Lifecycle a AppCompat
-keep class androidx.core.** { *; }
-dontwarn androidx.core.**
-keep class androidx.lifecycle.** { *; }
-dontwarn androidx.lifecycle.**
-keep class androidx.appcompat.** { *; }
-dontwarn androidx.appcompat.**

#Material3 Design
-keep class com.google.android.material.** { *; }
-dontwarn com.google.android.material.**


#ConstraintLayout
-keep class androidx.constraintlayout.** { *; }
-dontwarn androidx.constraintlayout.**

# Udržení všech datových tříd (data class)
-keepattributes *Annotation*
-keep class kotlin.Metadata { *; }
-keep class kotlin.reflect.** { *; }
-keepclassmembers class ** {
    @kotlin.Metadata *;
}

# Zabránění odstranění anonymních tříd v Kotlinu
-keepclassmembers class * {
    void *(kotlin.jvm.functions.Function*);
}

# Coroutines
-dontwarn kotlinx.coroutines.**
-keep class kotlinx.coroutines.** { *; }

# Ktor
-dontwarn io.ktor.**
-keep class io.ktor.** { *; }

#Hilt
-keepattributes *Annotation*
-keep class dagger.hilt.** { *; }
-dontwarn dagger.hilt.**
-keep class com.google.dagger.hilt.** { *; }
-dontwarn com.google.dagger.hilt.**
-keep class javax.inject.** { *; }
-dontwarn javax.inject.**

#Coil
-keep class coil.** { *; }
-dontwarn coil.**
-keep class okhttp3.** { *; }
-dontwarn okhttp3.**