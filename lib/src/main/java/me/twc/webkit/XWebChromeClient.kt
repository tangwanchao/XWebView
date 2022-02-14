package me.twc.webkit

import android.webkit.WebChromeClient
import android.webkit.WebView
import androidx.core.view.isVisible

/**
 * @author 唐万超
 * @date 2022/01/19
 */
open class XWebChromeClient : WebChromeClient() {

    /**
     * 如果没有 http 异常,当进度条到 100 的时候自动展示正文 View
     */
    override fun onProgressChanged(view: WebView, newProgress: Int) {
        super.onProgressChanged(view, newProgress)
        if (view !is XWebView) return

        val webState = view.getWebState()
        if(webState != null && !view.isError()){
            if(newProgress == 100){
                view.isVisible = true
                webState.showContent()
            }else{
                webState.showLoading(newProgress)
            }
        }
    }
}