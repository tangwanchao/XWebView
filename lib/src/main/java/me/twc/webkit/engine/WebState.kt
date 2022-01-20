package me.twc.webkit.engine

import androidx.annotation.IntRange

/**
 * @author 唐万超
 * @date 2022/01/19
 */
interface WebState {

    var mRetryListener: (() -> Unit)?

    /**
     * 展示异常 UI
     */
    fun showError()

    /**
     * 展示进度
     */
    fun showLoading(@IntRange(from = 0, to = 100) percent: Int)

    /**
     * 展示正文
     */
    fun showContent()
}