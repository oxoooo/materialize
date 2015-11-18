-keepattributes LineNumberTable

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

# realm
-keep class io.realm.annotations.RealmModule
-keep @io.realm.annotations.RealmModule class *
-keep class io.realm.internal.Keep
-keep @io.realm.internal.Keep class *
-dontwarn javax.**
-dontwarn io.realm.**

# all in all
-keepnames class * { *; }
