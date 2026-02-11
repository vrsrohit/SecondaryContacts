# Add project specific ProGuard rules here.

# ============================================
# Room Database
# ============================================
-keep class * extends androidx.room.RoomDatabase
-keep @androidx.room.Entity class *
-keep @androidx.room.Dao class *
-keepclassmembers class * {
    @androidx.room.* <methods>;
}

# Keep data classes used by Room
-keep class com.rohit.secondarycontacts.data.Contact { *; }
-keep class com.rohit.secondarycontacts.data.ContactIO { *; }

# ============================================
# Coil Image Loading
# ============================================
-keep class coil.** { *; }
-dontwarn coil.**

# ============================================
# Kotlin Coroutines
# ============================================
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}
-keepclassmembers class kotlinx.coroutines.** {
    volatile <fields>;
}

# ============================================
# Kotlin Serialization (if used)
# ============================================
-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.serialization.AnnotationsKt

# ============================================
# Jetpack Compose
# ============================================
-dontwarn androidx.compose.**
-keep class androidx.compose.** { *; }

# ============================================
# AndroidX Lifecycle / ViewModel
# ============================================
-keep class * extends androidx.lifecycle.ViewModel { <init>(...); }
-keep class * extends androidx.lifecycle.AndroidViewModel { <init>(...); }

# ============================================
# AndroidX Navigation
# ============================================
-keep class * extends androidx.navigation.Navigator

# ============================================
# General Android
# ============================================
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile
