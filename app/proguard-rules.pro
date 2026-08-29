# ==============================================================================
# ProGuard / R8 Optimization & Obfuscation Rules for Helpdesk Android App
# ==============================================================================

# Attributes
-keepattributes *Annotation*,Signature,InnerClasses,EnclosingMethod,SourceFile,LineNumberTable

# Kotlinx Serialization
-keepclassmembers class * {
    @kotlinx.serialization.Serializable <fields>;
    public static *** Companion;
}
-keepclasseswithmembers class * {
    public static final *** Companion;
}
-keepclassmembers class * implements kotlinx.serialization.KSerializer {
    public static *** INSTANCE;
}
-keep class * implements kotlinx.serialization.KSerializer {
    <init>(...);
}

# Domain & Data Models
-keep class com.helpdesk.app.domain.model.** { *; }
-keep class com.helpdesk.app.data.remote.dto.** { *; }

# Retrofit & OkHttp
-keepclassmembers,allowobfuscation interface * {
    @retrofit2.http.* <methods>;
}
-dontwarn okhttp3.**
-dontwarn retrofit2.**
-dontwarn okio.**

# Koin Dependency Injection
-keep class * extends org.koin.core.module.Module { *; }
-keep class org.koin.** { *; }
-dontwarn org.koin.**

# Kotlin Coroutines
-keepclassmembers class kotlinx.coroutines.** { *; }
-dontwarn kotlinx.coroutines.**

# Android Jetpack Compose
-keep class androidx.compose.** { *; }
-dontwarn androidx.compose.**

# Jetpack DataStore
-keep class androidx.datastore.** { *; }
