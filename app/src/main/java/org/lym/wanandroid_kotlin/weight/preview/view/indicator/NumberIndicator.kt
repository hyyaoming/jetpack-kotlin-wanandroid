package org.lym.wanandroid_kotlin.weight.preview.view.indicator

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView
import androidx.viewpager.widget.ViewPager
import androidx.viewpager.widget.ViewPager.OnPageChangeListener
import java.util.*

/**
 * 数字索引指示器
 */
class NumberIndicator @JvmOverloads constructor(
    context: Context?,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : AppCompatTextView(context, attrs, defStyleAttr) {
    private var mViewPager: ViewPager? = null
    private val mInternalPageChangeListener: OnPageChangeListener = object : OnPageChangeListener {
        override fun onPageScrolled(
            position: Int,
            positionOffset: Float,
            positionOffsetPixels: Int
        ) {
        }

        override fun onPageSelected(position: Int) {
            if (mViewPager!!.adapter == null || mViewPager!!.adapter!!.count <= 0) return
            text = String.format(
                Locale.getDefault(),
                STR_NUM_FORMAT,
                position + 1,
                mViewPager!!.adapter!!.count
            )
        }

        override fun onPageScrollStateChanged(state: Int) {}
    }

    private fun initNumberIndicator() {
        setTextColor(Color.WHITE)
        textSize = 18f
    }

    fun setViewPager(viewPager: ViewPager?) {
        if (viewPager != null && viewPager.adapter != null) {
            mViewPager = viewPager
            mViewPager!!.removeOnPageChangeListener(mInternalPageChangeListener)
            mViewPager!!.addOnPageChangeListener(mInternalPageChangeListener)
            mInternalPageChangeListener.onPageSelected(mViewPager!!.currentItem)
        }
    }

    companion object {
        private const val STR_NUM_FORMAT = "%s/%s"
    }

    init {
        initNumberIndicator()
    }
}