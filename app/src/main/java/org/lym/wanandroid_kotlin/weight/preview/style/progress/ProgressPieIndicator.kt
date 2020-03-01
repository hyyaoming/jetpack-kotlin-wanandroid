package org.lym.wanandroid_kotlin.weight.preview.style.progress

import android.graphics.Color
import android.util.SparseArray
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import com.blankj.utilcode.util.SizeUtils
import com.filippudak.ProgressPieView.ProgressPieView
import org.lym.wanandroid_kotlin.weight.preview.style.IProgressIndicator
import java.util.*

/**
 * 图片加载时使用饼状并带进度百分比显示的进度组件
 */
class ProgressPieIndicator : IProgressIndicator {
    private val progressPieArray = SparseArray<ProgressPieView>()

    override fun attach(position: Int, parent: FrameLayout) {
        val context = parent.context
        val progressSize = SizeUtils.dp2px(50f)
        val progressLp = FrameLayout.LayoutParams(
            progressSize, progressSize
        )
        progressLp.gravity = Gravity.CENTER
        val progressPieView = ProgressPieView(context)
        progressPieView.setTextSize(13)
        progressPieView.setStrokeWidth(1)
        progressPieView.textColor = Color.WHITE
        progressPieView.progressFillType = ProgressPieView.FILL_TYPE_RADIAL
        progressPieView.backgroundColor = Color.TRANSPARENT
        progressPieView.progressColor = Color.parseColor("#BBFFFFFF")
        progressPieView.strokeColor = Color.WHITE
        progressPieView.layoutParams = progressLp
        parent.addView(progressPieView, parent.childCount)
        progressPieArray.put(position, progressPieView)
    }

    override fun hideView(position: Int) {
        progressPieArray[position]?.let {
            it.visibility = View.GONE
        }
    }

    override fun onStart(position: Int) {
        val progressPieView = progressPieArray[position]
        progressPieView.progress = 0
        progressPieView.text = String.format(Locale.getDefault(), "%d%%", 0)
    }

    override fun onProgress(position: Int, progress: Int) {
        if (progress < 0 || progress > 100) {
            return
        }
        val progressPieView = progressPieArray[position]
        progressPieView.progress = progress
        progressPieView.text = String.format(
            Locale.getDefault(),
            "%d%%",
            progress
        )
    }

    override fun onFinish(position: Int) {
        progressPieArray[position]?.let {
            val viewGroup = it.parent as ViewGroup
            viewGroup.removeView(it)
        }
    }
}