package me.twc.webkit.demo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.webkit.WebView
import me.twc.webkit.XWebView
import me.twc.webkit.XWebViewClient
import me.twc.webkit.demo.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private val mBinding by lazy { ActivityMainBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(mBinding.root)
        mBinding.webView.loadUrl("https://www.baidu.com")
    }

    override fun onBackPressed() {
        if(mBinding.webView.canGoBack()){
            mBinding.webView.goBack()
            return
        }
        super.onBackPressed()
    }
}