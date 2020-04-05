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
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : AppCompatTextView(context, attrs, defStyleAttr) {
    private lateinit var viewPager: ViewPager
    private val mInternalPageChangeListener: OnPageChangeListener = object :
        ViewPager.SimpleOnPageChangeListener() {
        override fun onPageSelected(position: Int) {
            if (!this@NumberIndicator::viewPager.isInitialized) {
                return
            }
            if (viewPager.adapter == null || viewPager.adapter!!.count <= 0) return
            text = String.format(
                Locale.getDefault(),
                STR_NUM_FORMAT,
                position + 1,
                viewPager.adapter!!.count
            )
        }
    }

    private fun initNumberIndicator() {
        setTextColor(Color.WHITE)
        textSize = 18f
    }

    fun setViewPager(viewPager: ViewPager) {
        viewPager.adapter?.let {
            this.viewPager = viewPager
            viewPager.removeOnPageChangeListener(mInternalPageChangeListener)
            viewPager.addOnPageChangeListener(mInternalPageChangeListener)
            mInternalPageChangeListener.onPageSelected(viewPager.currentItem)
        }
    }

    companion object {
        private const val STR_NUM_FORMAT = "%s/%s"
    }

    init {
        initNumberIndicator()
    }
}