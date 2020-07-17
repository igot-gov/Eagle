## Add project specific ProGuard rules here.
## You can control the set of applied configuration files using the
## proguardFiles setting in build.gradle.
##
## For more details, see
##   http://developer.android.com/guide/developing/tools/proguard.html
#
## If your project uses WebView with JS, uncomment the following
## and specify the fully qualified class name to the JavaScript interface
## class:
##-keepclassmembers class fqcn.of.javascript.interface.for.webview {
##   public *;
##}

-dontwarn

-keepattributes Signature
-keepattributes SetJavaScriptEnabled
-keepattributes JavascriptInterface
-keepattributes InlinedApi
-keepattributes SourceFile,LineNumberTable
-keepattributes *Annotation*
-keepattributes EnclosingMethod

-keepclassmembers class * {
    @android.webkit.JavascriptInterface <methods>;
}
-keepclassmembers class * {
    @android.webkit.JavascriptInterface <methods>;
}
-keepclassmembers class **.*$ChatBotBridge {
    *;
}
-keepclassmembers class **.*$DownloadsBridge {
    *;
}
-keepclassmembers class **.*$PlayerBridge {
    *;
}
-keepclassmembers class **.*$TOCBridge {
    *;
}
-keepclassmembers class **.*$WebViewBridgeJava {
    *;
}
-keepclassmembers class **.*$JavaScriptInterface {
    *;
}

-keep public class **.*$ChatBotBridge
-keep public class **.*$DownloadsBridge
-keep public class **.*$PlayerBridge
-keep public class **.*$TOCBridge
-keep public class **.*$WebViewBridgeJava
-keep public class **.*$JavaScriptInterface


#
## Uncomment this to preserve the line number information for
## debugging stack traces.
##-keepattributes SourceFile,LineNumberTable
#
## If you keep the line number information, uncomment this to
## hide the original source file name.
##-renamesourcefileattribute SourceFile
-assumenosideeffects class android.util.Log {
    public static boolean isLoggable(java.lang.String, int);
    public static int v(...);
    public static int i(...);
    public static int w(...);
    public static int d(...);
}
#
## Okio
#-dontwarn okio.**
#

-dontwarn retrofit2.**
-keep class retrofit2.** { *; }
-keepattributes Signature
-keepattributes Exceptions

-keepclasseswithmembers class * {
    @retrofit2.http.* <methods>;
}
## Retrofit


-keepnames class okhttp3.internal.publicsuffix.PublicSuffixDatabase

# for room persistence
-dontwarn android.arch.util.paging.CountedDataSource
-dontwarn android.arch.persistence.room.paging.LimitOffsetDataSource
-dontwarn javax.servlet.**
-dontwarn org.joda.time.**
-dontwarn org.w3c.dom.**

-dontwarn okio.**
-dontwarn com.squareup.okhttp3.**
-keep interface com.squareup.okhttp3.* { *; }
-dontwarn javax.annotation.Nullable
-dontwarn javax.annotation.ParametersAreNonnullByDefault
-ignorewarnings

