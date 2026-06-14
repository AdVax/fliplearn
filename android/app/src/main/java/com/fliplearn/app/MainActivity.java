package com.fliplearn.app;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowInsets;
import android.view.WindowInsetsController;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

/**
 * FlipLearn — Full-screen WebView wrapper for the PWA.
 *
 * What this does:
 *  • Loads index.html from the app's assets folder
 *  • Enables JavaScript + DOM Storage (localStorage) for the app's data
 *  • Hides system bars for an immersive experience
 *  • Intercepts the back button so it navigates within the WebView
 *  • Sets the background to the app's dark colour so there is no
 *    white flash during loading
 */
public class MainActivity extends Activity {

    private WebView webView;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // ── Immersive full-screen ────────────────────────────────
        setupFullscreen();

        // ── WebView ─────────────────────────────────────────────
        webView = new WebView(this);
        webView.setBackgroundColor(Color.parseColor("#0b0b12")); // app's dark BG

        setContentView(webView);

        // ── WebView settings ────────────────────────────────────
        @SuppressLint("SetJavaScriptEnabled")
        WebSettings s = webView.getSettings();

        s.setJavaScriptEnabled(true);           // required — the app is JS-driven
        s.setDomStorageEnabled(true);           // localStorage/sessionStorage
        s.setDatabaseEnabled(true);             // Web SQL (legacy, safe to enable)
        s.setAllowFileAccessFromFileURLs(true); // allow assets to read each other
        s.setAllowUniversalAccessFromFileURLs(true);
        s.setCacheMode(WebSettings.LOAD_DEFAULT);
        s.setMediaPlaybackRequiresUserGesture(false);
        s.setTextZoom(100);                     // respect system font size but cap at 100%
        s.setSupportZoom(false);                // prevent pinch-to-zoom (it's a card app)
        s.setBuiltInZoomControls(false);
        s.setDisplayZoomControls(false);
        s.setMixedContentMode(WebSettings.MIXED_CONTENT_COMPATIBILITY_MODE);

        // ── WebViewClient ───────────────────────────────────────
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                // Keep all navigation inside the WebView
                return false;
            }
        });

        // ── Load the PWA ────────────────────────────────────────
        webView.loadUrl("file:///android_asset/index.html");
    }

    // ── Back button: navigate in WebView before exiting ─────────
    @SuppressWarnings("deprecation") // onBackPressed is fine for this use case
    @Override
    public void onBackPressed() {
        if (webView != null && webView.canGoBack()) {
            webView.goBack();
        } else {
            super.onBackPressed();
        }
    }

    // ── Resume: re-apply immersive mode (system may restore bars) ─
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            setupFullscreen();
        }
    }

    // ────────────────────────────────────────────────────────────
    // Immersive full-screen helper
    //  • API 30+ → WindowInsetsController (current API)
    //  • API 24-29 → setSystemUiVisibility (deprecated but functional)
    // ────────────────────────────────────────────────────────────
    private void setupFullscreen() {
        getWindow().setStatusBarColor(Color.TRANSPARENT);
        getWindow().setNavigationBarColor(Color.TRANSPARENT);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            // API 30+: disable system-enforced contrast so bars stay fully transparent
            // (replaces android:navigationBarContrastEnforced in values-v30/styles.xml)
            getWindow().setNavigationBarContrastEnforced(false);
            getWindow().setStatusBarContrastEnforced(false);

            getWindow().setDecorFitsSystemWindows(false);
            WindowInsetsController ctrl = getWindow().getInsetsController();
            if (ctrl != null) {
                ctrl.hide(WindowInsets.Type.statusBars()
                        | WindowInsets.Type.navigationBars());
                ctrl.setSystemBarsBehavior(
                        WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE);
            }
        } else {
            // API 24-29
            //noinspection deprecation
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }
    }
}
