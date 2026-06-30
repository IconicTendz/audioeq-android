# AudioEQ ProGuard Rules

# Keep Gson serialization models
-keepclassmembers class com.audioeq.data.model.** { *; }
-keepclassmembers class com.audioeq.data.db.entity.** { *; }

# Keep Room entities
-keep class * extends androidx.room.RoomDatabase { *; }
-keep @androidx.room.Entity class * { *; }
-keep @androidx.room.Dao class * { *; }

# Keep Native JNI methods
-keepclasseswithmembernames class com.audioeq.audio.DspBridge {
    native <methods>;
}

# Keep Compose
-dontwarn androidx.compose.**

# Keep Oboe
-keep class com.google.oboe.** { *; }
-dontwarn com.google.oboe.**

# Keep Google Play Services
-keep class com.google.android.gms.** { *; }
-dontwarn com.google.android.gms.**

# Keep kotlinx serialization
-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.serialization.AnnotationsKt
-keepclassmembers class kotlinx.serialization.json.** { *** Companion; }
-keepclasseswithmembers class kotlinx.serialization.json.** {
    kotlinx.serialization.KSerializer serializer(...);
}

# General Android rules
-keepattributes Signature
-keepattributes *Annotation*
-keep class * extends android.app.Service { *; }
-keep class * extends android.appwidget.AppWidgetProvider { *; }
-keep class * extends android.service.quicksettings.TileService { *; }
-keep class * extends android.accessibilityservice.AccessibilityService { *; }

-dontwarn okhttp3.**
-dontwarn okio.**
