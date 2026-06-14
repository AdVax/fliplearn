# FlipLearn ProGuard Rules
# (minifyEnabled is false for debug builds so these only apply to release)

# Keep MainActivity and WebView classes
-keep class com.fliplearn.app.** { *; }
-keepclassmembers class * extends android.webkit.WebViewClient { *; }
-keepclassmembers class * extends android.webkit.WebChromeClient { *; }

# Keep JavaScript interface methods (if any are added later)
-keepclassmembers class * {
    @android.webkit.JavascriptInterface <methods>;
}
