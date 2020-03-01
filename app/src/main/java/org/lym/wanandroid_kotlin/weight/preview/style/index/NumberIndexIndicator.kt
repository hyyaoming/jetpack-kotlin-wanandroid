package org.lym.wanandroid_kotlin.weight.preview.style.index

import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.viewpager.widget.ViewPager
import org.lym.wanandroid_kotlin.weight.preview.style.IIndexIndicator
import org.lym.wanandroid_kotlin.weight.preview.view.indicator.NumberIndicator

/**
 * 图片翻页时使用 [NumberIndicator] 去指示当前图片的位置
 *
 *
 * Created by Hitomis on 2017/4/23 0023.
 *
 *
 * email: 196425254@qq.com
 */
class NumberIndexIndicator : IIndexIndicator {
    private var numberIndicator: NumberIndicator? = null

    override fun attach(parent: FrameLayout) {
        val indexLp = FrameLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        indexLp.gravity = Gravity.TOP or Gravity.CENTER_HORIZONTAL
        indexLp.topMargin = 30
        numberIndicator = NumberIndicator(parent.context).apply {
            this.layoutParams = indexLp
            parent.addView(this)
        }
    }

    override fun onShow(viewPager: ViewPager) {
        numberIndicator?.let {
            it.visibility = View.VISIBLE
            it.setViewPager(viewPager)
        }
    }

    override fun onHide() {
        numberIndicator?.let {
            it.visibility = View.GONE
        }
    }

    override fun onRemove() {
        numberIndicator?.let {
            val viewGroup = it.parent as ViewGroup
            viewGroup.removeView(numberIndicator)
        }
    }
}