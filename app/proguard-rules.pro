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

# ---------------------------------------------------------------------------
# Benchmark build type uses R8. Add minimal keep rules for libraries that
# rely on reflection or are looked up by name (Haze, Navigation3, Media3),
# plus the launcher activity so Intent extras keep their wiring.
# ---------------------------------------------------------------------------
-keep class com.kartollika.mobiussharedtransitions.MainActivity { *; }
-keep class dev.chrisbanes.haze.** { *; }
-keep class androidx.navigation3.** { *; }
-keep class androidx.media3.exoplayer.** { *; }
-keep class androidx.media3.ui.** { *; }
-dontwarn dev.chrisbanes.haze.**
-dontwarn androidx.navigation3.**
-dontwarn androidx.media3.**