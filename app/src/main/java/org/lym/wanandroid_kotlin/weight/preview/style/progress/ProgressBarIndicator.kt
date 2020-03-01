package org.lym.wanandroid_kotlin.weight.preview.style.progress

import android.util.SparseArray
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ProgressBar
import com.blankj.utilcode.util.SizeUtils
import org.lym.wanandroid_kotlin.weight.preview.style.IProgressIndicator

/**
 * 图片加载时使用 Android 默认的 ProgressBar
 */
class ProgressBarIndicator : IProgressIndicator {

    private val progressBarArray = SparseArray<ProgressBar>()

    override fun attach(position: Int, parent: FrameLayout) {
        val context = parent.context
        val progressSize = SizeUtils.dp2px(50f)
        val progressLp = FrameLayout.LayoutParams(
            progressSize, progressSize
        )
        progressLp.gravity = Gravity.CENTER
        val progressBar = ProgressBar(context)
        progressBar.layoutParams = progressLp
        parent.addView(progressBar, parent.childCount)
        progressBarArray.put(position, progressBar)
    }

    override fun hideView(position: Int) {
        progressBarArray[position]?.let {
            it.visibility = View.GONE
        }
    }

    override fun onStart(position: Int) {

    }

    override fun onProgress(position: Int, progress: Int) {

    }

    override fun onFinish(position: Int) {
        progressBarArray[position]?.let {
            val viewGroup = it.parent as ViewGroup
            viewGroup.removeView(it)
        }
    }
}