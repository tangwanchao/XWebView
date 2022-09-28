package me.twc.webkit

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Configuration
import android.content.res.Resources
import android.os.Build
import android.util.AttributeSet
import android.view.ViewGroup
import android.webkit.WebSettings
import android.webkit.WebView
import androidx.core.view.children
import me.twc.webkit.engine.WebState
import java.io.File

/**
 * https://stackoverflow.com/questions/58028821/webview-crash-on-android-5-5-1-api-21-22-resourcesnotfoundexception-string-r
 */
private fun Context.getLollipopFixWebView(): Context {
    return if (Build.VERSION.SDK_INT in 21..22) {
        createConfigurationContext(Configuration())
    } else this
}

/**
 * 设置当前 WebView 处于异常状态
 *
 * @param failingUrl 加载异常的 url
 */
internal fun XWebView.setError(failingUrl:String?){
    setTag(R.id.failing_url, failingUrl)
}

/**
 * @return [true : 当前 WebView 处于异常状态]
 *         [false: 其他情况]
 */
fun XWebView.isError():Boolean{
    return getTag(R.id.failing_url) != null
}

/**
 * @author 唐万超
 * @date 2022/01/19
 */
@SuppressLint("SetJavaScriptEnabled")
open class XWebView @JvmOverloads constructor(
    context: Context,
    attr: AttributeSet? = null,
    defStyle: Int = Resources.getSystem().getIdentifier("webViewStyle", "attr", "android")
) : WebView(context.getLollipopFixWebView(), attr, defStyle) {

    init {
        if(!isInEditMode) {
            val settings = this.settings
            // 禁用缩放
            settings.setSupportZoom(false)
            // 禁用悬浮在 WebView 上面的缩放控件
            settings.builtInZoomControls = false
            // 禁用显示缩放控件
            settings.displayZoomControls = false
            // 缩放页面到控件大小
            settings.loadWithOverviewMode = true
            // 启用视口支持
            settings.useWideViewPort = true
            // 是否支持多窗口
            settings.setSupportMultipleWindows(false)
            @Suppress("DEPRECATION")
            // 启用 js
            settings.javaScriptEnabled = true
            // url 中的 js 无法访问其他 url
            settings.allowFileAccessFromFileURLs = false
            // 禁用 file:/// 中的 js 访问其他 file:///
            settings.allowUniversalAccessFromFileURLs = false
            // 应用缓存 api 可以使用
            settings.setAppCacheEnabled(true)
            // 设置缓存路径
            settings.setAppCachePath(getContext().cacheDir.absolutePath + File.separator + "web")
            // dom 储存 api 可用
            settings.domStorageEnabled = true
            // 允许定位
            settings.setGeolocationEnabled(true)
            // js 无法打开窗口
            settings.javaScriptCanOpenWindowsAutomatically = false
            // 无缓存
            settings.cacheMode = WebSettings.LOAD_DEFAULT
            // 允许从不安全来源加载资源
            settings.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
            // 允许访问ContentUrl
            settings.allowContentAccess = true
            // 允许访问系统文件(file:///android_asset和file:///android_res始终可访问)
            settings.allowFileAccess = true
            // 数据库存储 api 可用
            settings.databaseEnabled = true
            // 文本缩放百分比
            settings.textZoom = 100

            // 修复 5.x bug
            // https://stackoverflow.com/questions/58028821/webview-crash-on-android-5-5-1-api-21-22-resourcesnotfoundexception-string-r
            isFocusable = true
            isFocusableInTouchMode = true
            bindDefaultClient()
        }
    }

    /**
     * 绑定默认的 client
     */
    private fun bindDefaultClient() {
        webViewClient = XWebViewClient()
        webChromeClient = XWebChromeClient()
    }

    /**
     * 在原有 UserAgent 后面追加字符串
     *
     * @param appendUserAgent 需要追加的 UserAgent 字符串
     */
    @Suppress("unused")
    open fun appendUserAgent(appendUserAgent: String) {
        settings.apply {
            this.userAgentString = this.userAgentString + appendUserAgent
        }
    }


    //<editor-fold desc="WebState">
    private var mWebState: WebState? = null

    fun getWebState(): WebState? {
        val webState = mWebState
        if (webState != null) {
            return webState
        }
        var parent = parent as? ViewGroup
        val excludes = hashSetOf<ViewGroup>()
        while (parent != null) {
            val findOr = findWebState(parent,excludes)
            if (findOr != null) {
                mWebState = findOr
                findOr.mRetryListener = {
                    setTag(R.id.failing_url, null)
                    reload()
                }
                return findOr
            }else{
                parent = parent.parent as? ViewGroup
            }
        }
        return null
    }

    /**
     * 遍历查找当前布局中的 [WebState]
     */
    private fun findWebState(parent: ViewGroup?,excludes:HashSet<ViewGroup>): WebState? {
        if (parent == null) return null
        excludes.add(parent)
        // 同级查找
        for (child in parent.children) {
            if (child is WebState) {
                return child
            }
        }
        // 子级查找
        for(child in parent.children) {
            if (child is ViewGroup && !excludes.contains(child)) {
                val findOr = findWebState(child,excludes)
                if (findOr != null) {
                    return findOr
                }
            }
        }
        return null
    }
    //</editor-fold>
}