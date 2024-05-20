package com.dirror.music.ui.LocalJavaScript

import android.webkit.JavascriptInterface
import android.webkit.WebView
import android.widget.Toast

class MyJavaScriptInterface(private val webView: WebView) {

    @JavascriptInterface
    fun showToast(message: String) {
        Toast.makeText(webView.context, message, Toast.LENGTH_SHORT).show()
    }
}