package me.twc.webkit.demo

import android.content.Context
import android.util.AttributeSet
import android.view.View
import com.github.nukc.stateview.StateView
import me.twc.webkit.engine.WebState

/**
 * @author 唐万超
 * @date 2022/01/19
 */
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