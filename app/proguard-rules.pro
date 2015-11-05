# retrolambda
-dontwarn java.lang.invoke.*

# okhttp
-dontwarn okio.**

# retrofit
-dontwarn retrofit.**
-keep class retrofit.** { *; }
-keepattributes Signature
-keepattributes Exceptions

# rx
-dontwarn rx.**
-keepclassmembers class rx.** { *; }

# support library
-keep class android.support.v7.widget.LinearLayoutManager { *; }

# data binding
-keep class ooo.oxo.apps.materialize.databinding.** { *; }

# glide
-keep class ooo.oxo.apps.materialize.io.DrawableGlideMode { *; }

# all in all
-keepnames class * { *; }
