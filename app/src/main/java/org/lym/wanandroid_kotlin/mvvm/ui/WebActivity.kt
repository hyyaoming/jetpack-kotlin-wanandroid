package org.lym.wanandroid_kotlin.mvvm.ui

import android.annotation.SuppressLint
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import kotlinx.android.synthetic.main.activity_web.*
import org.lym.wanandroid_kotlin.R
import org.lym.wanandroid_kotlin.common.WEB_TITLE
import org.lym.wanandroid_kotlin.common.WEB_URL

/**
 * Web页面展示
 *
 * author: liyaoming
 * date: 2020/4/3-5:44 PM
 */
class WebActivity : BaseActivity() {

    override fun subscribeUI() {
        val bundle = intent.extras ?: return
        val url = bundle.getString(WEB_URL, "")
        webView.loadUrl(url)
        title_bar.titleTextView.text = bundle.getString(WEB_TITLE, "")
        title_bar.leftIconView.setOnClickListener { finish() }
    }

    override fun initView() {
        initWebView()
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun initWebView() {
        val webSettings: WebSettings = webView.settings
        webSettings.javaScriptEnabled = true
        //自适应屏幕
        webView.settings.layoutAlgorithm = WebSettings.LayoutAlgorithm.SINGLE_COLUMN
        webView.settings.loadWithOverviewMode = true

        webView.webViewClient = object : WebViewClient() {

            override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                return false
            }

            override fun shouldOverrideUrlLoading(
                view: WebView,
                request: WebResourceRequest
            ): Boolean {
                return false
            }
        }
    }

    override fun onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack()
            return
        }
        super.onBackPressed()
    }

    override fun getLayoutId() = R.layout.activity_web
}