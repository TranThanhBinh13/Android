package com.example.app_tv.activities

import android.os.Bundle
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import com.example.app_tv.R

class NewsDetailActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_URL = "com.example.app_tv.EXTRA_URL"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_news_detail)

        val webView: WebView = findViewById(R.id.webView)

        // Đảm bảo WebView có thể hiển thị nội dung HTML và JavaScript
        webView.settings.javaScriptEnabled = true
        webView.webViewClient = WebViewClient()

        // Lấy URL từ Intent
        val url = intent.getStringExtra(EXTRA_URL)
        if (url != null) {
            webView.loadUrl(url)
        }
    }
}
