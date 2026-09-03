# ==============================================================================
# ProGuard / R8 rules for Helpdesk Android App
# ==============================================================================

# Annotations & Kotlin metadata (needed by serialization & compose)
-keepattributes *Annotation*,Signature,InnerClasses,EnclosingMethod,SourceFile,LineNumberTable

# Kotlinx Serialization — keep all serializable types, companion objects, and serializers
-keep,allowobfuscation: @kotlinx.serialization.Serializable class * { *; }
-keepclassmembers,allowobfuscation: class **$$serializer {
    public static *** INSTANCE;
}
# Keep companion objects so that the generated serializer companions are reachable.
-keepclassmembers class * {
    @kotlinx.serialization.Serializable <fields>;
    public static *** Companion;
}

# Retrofit — keep interface method annotations only (R8 already keeps the
# interfaces). Allow obfuscation of the implementation class itself.
-keep,allowobfuscation interface * {
    @retrofit2.http.* <methods>;
}

# Koin — keep module classes and the runtime; instances / factory lambdas
# may be obfuscated.
-keep class * extends org.koin.core.module.Module { *; }
-keep class org.koin.** { *; }
-keepclassmembers class org.koin.** { *; }

# Kotlin Coroutines — keep the coroutine API surface; internal impls may be
# obfuscated by R8.
-keep,allowobfuscation class kotlinx.coroutines.** { *; }

# Jetpack DataStore — keep the preferences key classes that are accessed by
# name (KEY_* constants), plus the DataStore runtime.
-keepclassmembers class androidx.datastore.preferences.core.PreferencesKeys { *; }
-keep class androidx.datastore.** { *; }

# Do not warn about third-party libraries that ship only compiled classes.
-dontwarn okhttp3.**
-dontwarn retrofit2.**
-dontwarn okio.**