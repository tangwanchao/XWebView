# XWebView

XWebView 是为了快速使用 WebView

# 特点

- 支持默认初始化
- 设置默认 WebViewClient,XWebChromeClient
- WebView 状态用户友好拓展

# 用法

## 基本

和系统 Webkit 库用法完全相同

## WebView 状态拓展

1.状态 View 实现 WebState 接口
```kotlin
class WebStateView @JvmOverloads constructor(
    context: Context,
    attr: AttributeSet? = null,
    defStyle: Int = 0
) : StateView(
    context, attr, defStyle
), WebState {
    override var mRetryListener: (() -> Unit)? = null

    init {
        onRetryClickListener = object : OnRetryClickListener {
            override fun onRetryClick() {
                mRetryListener?.invoke()
            }
        }
        onInflateListener = object :OnInflateListener{
            override fun onInflate(layoutResource: Int, view: View) {
                if(layoutResource == R.layout.layout_error){
                    view.setOnClickListener { onRetryClickListener?.onRetryClick() }
                }
            }
        }
    }

    override fun showError() {
        showRetry()
    }

    override fun showLoading(percent: Int) {
        showLoading()
    }
}
```

2.保证状态 View 和 XWebView 在同一 rootView 下(层级随意,将遍历 XWebView 树中所有 View 进行查找)
```xml
<?xml version="1.0" encoding="utf-8"?>
<androidx.constraintlayout.widget.ConstraintLayout
    xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    tools:context=".MainActivity">

    <me.twc.webkit.XWebView
        android:id="@+id/web_view"
        android:layout_width="match_parent"
        android:layout_height="match_parent" />

    <me.twc.webkit.demo.WebStateView
        android:id="@+id/state_view"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        app:loadingResource="@layout/layout_loading"
        app:retryResource="@layout/layout_error" />
</androidx.constraintlayout.widget.ConstraintLayout>
```
3.加载链接
```kotlin
class MainActivity : AppCompatActivity() {

    private val mBinding by lazy { ActivityMainBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(mBinding.root)
        mBinding.webView.loadUrl("https://www.baidu.com")
    }
}
```

# 权限说明


权限 | 说明
---|---
android.permission.INTERNET | 使用网络

# 混淆规则

无