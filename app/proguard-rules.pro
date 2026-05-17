# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to rules specified
# in ${sdk.dir}/tools/proguard/proguard-android.txt

# Compose
-keep class androidx.compose.** { *; }
-keep class androidx.compose.ui.** { *; }
-keep class androidx.compose.runtime.** { *; }

# ViewModel
-keep class * extends androidx.lifecycle.ViewModel {
    <init>(...);
}
-keepclassmembers class * extends androidx.lifecycle.ViewModel {
    <methods>;
}

# Keep DataStore
-keep class androidx.datastore.** { *; }

# Keep Service classes
-keep class com.nightlight.app.service.** { *; }

# Keep Sensor managers
-keep class com.nightlight.app.sensor.** { *; }

# Keep Audio generator
-keep class com.nightlight.app.audio.** { *; }

# Keep Data classes
-keep class com.nightlight.app.data.** { *; }

# Keep ViewModel
-keep class com.nightlight.app.viewmodel.** { *; }

# Keep UI components
-keep class com.nightlight.app.ui.** { *; }

# Keep MainActivity
-keep class com.nightlight.app.MainActivity { *; }

# Keep enum classes (for DataStore serialization)
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

# Suppress warnings for unused library classes
-dontwarn androidx.compose.ui.platform.**
-dontwarn org.jetbrains.annotations.**
