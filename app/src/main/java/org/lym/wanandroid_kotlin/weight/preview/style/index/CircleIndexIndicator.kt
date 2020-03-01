package org.lym.wanandroid_kotlin.weight.preview.style.index

import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.viewpager.widget.ViewPager
import org.lym.wanandroid_kotlin.weight.preview.style.IIndexIndicator
import org.lym.wanandroid_kotlin.weight.preview.view.indicator.CircleIndicator

/**
 * 图片翻页时使用 [CircleIndicator] 去指示当前图片的位置
 */
class CircleIndexIndicator : IIndexIndicator {
    private var circleIndicator: CircleIndicator? = null

    override fun attach(parent: FrameLayout) {
        val indexLp = FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, 48)
        indexLp.gravity = Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL
        indexLp.bottomMargin = 10
        circleIndicator = CircleIndicator(parent.context).apply {
            this.gravity = Gravity.CENTER_VERTICAL
            this.layoutParams = indexLp
            parent.addView(this)
        }
    }

    override fun onShow(viewPager: ViewPager) {
        circleIndicator?.let {
            it.visibility = View.VISIBLE
            it.setViewPager(viewPager)
        }
    }

    override fun onHide() {
        circleIndicator?.let {
            it.visibility = View.GONE
        }
    }

    override fun onRemove() {
        circleIndicator?.let {
            val vg = it.parent as ViewGroup
            vg.removeView(circleIndicator)
        }
    }
}