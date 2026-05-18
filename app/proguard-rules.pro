# Project-specific ProGuard rules for Nightlight

# Compose - only keep what's needed for runtime reflection
-keep,allowobfuscation @interface androidx.compose.runtime.Composable
-keep class * {
    @androidx.compose.runtime.Composable <methods>;
}

# ViewModel - keep constructors for instantiation
-keepclassmembers class * extends androidx.lifecycle.ViewModel {
    <init>(...);
}

# DataStore
-keep class androidx.datastore.preferences.core.** { *; }

# App classes - keep public APIs
-keep public class com.nightwhite.app.MainActivity { *; }
-keep public class com.nightwhite.app.service.** { *; }
-keep public class com.nightwhite.app.viewmodel.** { *; }
-keep public class com.nightwhite.app.audio.** { *; }
-keep public class com.nightwhite.app.sensor.** { *; }
-keep public class com.nightwhite.app.data.** { *; }

# Enum classes (for DataStore serialization)
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

# Suppress known warnings
-dontwarn androidx.compose.ui.platform.**
-dontwarn org.jetbrains.annotations.**
