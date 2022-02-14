package me.twc.webkit

import android.os.Looper
import android.webkit.*
import androidx.core.view.isVisible

/**
 * @author 唐万超
 * @date 2022/01/19
 */
open class XWebViewClient : WebViewClient() {
    override fun onReceivedError(
        view: WebView,
        errorCode: Int,
        description: String?,
        failingUrl: String
    ) {
        onError(view, failingUrl)
    }

    override fun onReceivedHttpError(
        view: WebView,
        request: WebResourceRequest,
        errorResponse: WebResourceResponse?
    ) {
        if (request.isForMainFrame) {
            onError(view, request.url.toString())
        }
    }

    /**
     *
     * 处理页面加载异常
     *
     * 如果 webView 使用了 WebViewWrapper,自动弹出加载异常界面
     *
     * @see onReceivedHttpError
     * @see onReceivedError
     */
    open fun onError(
        view: WebView,
        failingUrl: String,
    ) {
        if (view is XWebView) {
            view.getWebState()?.let {
                // 设置 tag 标记这次请求是失败的
                view.setError(failingUrl)
                if (Looper.getMainLooper() == Looper.myLooper()) {
                    view.isVisible = false
                    it.showError()
                } else {
                    view.post {
                        view.isVisible = false
                        it.showError()
                    }
                }
            }
        }
    }
}