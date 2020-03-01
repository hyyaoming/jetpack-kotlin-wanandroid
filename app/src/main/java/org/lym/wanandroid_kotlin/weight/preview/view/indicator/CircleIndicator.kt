package org.lym.wanandroid_kotlin.weight.preview.view.indicator

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.database.DataSetObserver
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.view.animation.Interpolator
import android.widget.LinearLayout
import androidx.viewpager.widget.ViewPager
import androidx.viewpager.widget.ViewPager.OnPageChangeListener
import com.blankj.utilcode.util.SizeUtils
import org.lym.wanandroid_kotlin.R
import kotlin.math.abs

/**
 * 圆形小点索引指示器
 */
class CircleIndicator @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {
    private var mViewpager: ViewPager? = null
    private var mIndicatorBackground: GradientDrawable? = null
    private var mAnimatorOut: Animator? = null
    private var mAnimatorIn: Animator? = null
    private var mImmediateAnimatorOut: Animator? = null
    private var mImmediateAnimatorIn: Animator? = null
    private var mIndicatorMargin = -1
    private var mIndicatorWidth = -1
    private var mIndicatorHeight = -1
    private var mLastPosition = -1
    private val mInternalPageChangeListener: OnPageChangeListener = object : OnPageChangeListener {
        override fun onPageScrolled(
            position: Int,
            positionOffset: Float,
            positionOffsetPixels: Int
        ) {
        }

        override fun onPageSelected(position: Int) {
            if (mViewpager!!.adapter == null || mViewpager!!.adapter!!.count <= 0) {
                return
            }
            if (mAnimatorIn!!.isRunning) {
                mAnimatorIn!!.end()
                mAnimatorIn!!.cancel()
            }
            if (mAnimatorOut!!.isRunning) {
                mAnimatorOut!!.end()
                mAnimatorOut!!.cancel()
            }
            var currentIndicator: View? = null
            if (mLastPosition >= 0 && getChildAt(mLastPosition).also {
                    currentIndicator = it
                } != null) {
                currentIndicator?.background = mIndicatorBackground
                mAnimatorIn!!.setTarget(currentIndicator)
                mAnimatorIn!!.start()
            }
            val selectedIndicator = getChildAt(position)
            if (selectedIndicator != null) {
                selectedIndicator.background = mIndicatorBackground
                mAnimatorOut!!.setTarget(selectedIndicator)
                mAnimatorOut!!.start()
            }
            mLastPosition = position
        }

        override fun onPageScrollStateChanged(state: Int) {}
    }
    val dataSetObserver: DataSetObserver = object : DataSetObserver() {
        override fun onChanged() {
            super.onChanged()
            if (mViewpager == null || mViewpager!!.adapter == null) {
                return
            }
            val newCount = mViewpager!!.adapter!!.count
            val currentCount = childCount
            mLastPosition = when {
                newCount == currentCount -> { // No change
                    return
                }
                mLastPosition < newCount -> {
                    mViewpager!!.currentItem
                }
                else -> {
                    -1
                }
            }
            createIndicators()
        }
    }

    private fun init(
        context: Context,
        attrs: AttributeSet?
    ) {
        mIndicatorBackground = GradientDrawable()
        mIndicatorBackground!!.shape = GradientDrawable.OVAL
        mIndicatorBackground!!.setColor(Color.WHITE)
        handleTypedArray(context, attrs)
        checkIndicatorConfig()
    }

    private fun handleTypedArray(
        context: Context,
        attrs: AttributeSet?
    ) {
        if (attrs == null) {
            return
        }
        val typedArray =
            context.obtainStyledAttributes(attrs, R.styleable.CircleIndicator)
        mIndicatorWidth = typedArray.getDimensionPixelSize(R.styleable.CircleIndicator_ci_width, -1)
        mIndicatorHeight =
            typedArray.getDimensionPixelSize(R.styleable.CircleIndicator_ci_height, -1)
        mIndicatorMargin =
            typedArray.getDimensionPixelSize(R.styleable.CircleIndicator_ci_margin, -1)
        val orientation = typedArray.getInt(R.styleable.CircleIndicator_ci_orientation, -1)
        setOrientation(if (orientation == VERTICAL) VERTICAL else HORIZONTAL)
        val gravity = typedArray.getInt(R.styleable.CircleIndicator_ci_gravity, -1)
        setGravity(if (gravity >= 0) gravity else Gravity.CENTER)
        typedArray.recycle()
    }

    /**
     * Create and configure Indicator in Java code.
     */
    fun configureIndicator(
        indicatorWidth: Int,
        indicatorHeight: Int,
        indicatorMargin: Int
    ) {
        mIndicatorWidth = indicatorWidth
        mIndicatorHeight = indicatorHeight
        mIndicatorMargin = indicatorMargin
        checkIndicatorConfig()
    }

    private fun checkIndicatorConfig() {
        mIndicatorWidth =
            if (mIndicatorWidth < 0) SizeUtils.dp2px(DEFAULT_INDICATOR_WIDTH.toFloat()) else mIndicatorWidth
        mIndicatorHeight =
            if (mIndicatorHeight < 0) SizeUtils.dp2px(DEFAULT_INDICATOR_WIDTH.toFloat()) else mIndicatorHeight
        mIndicatorMargin =
            if (mIndicatorMargin < 0) SizeUtils.dp2px(DEFAULT_INDICATOR_WIDTH.toFloat()) else mIndicatorMargin
        mAnimatorOut = createAnimatorOut()
        mImmediateAnimatorOut = createAnimatorOut()
        mImmediateAnimatorOut!!.duration = 0
        mAnimatorIn = createAnimatorIn()
        mImmediateAnimatorIn = createAnimatorIn()
        mImmediateAnimatorIn!!.duration = 0
    }

    @SuppressLint("ObjectAnimatorBinding")
    private fun createAnimatorOut(): Animator {
        val alphaAnima = ObjectAnimator.ofFloat(null, "alpha", .5f, 1f)
        val scaleX = ObjectAnimator.ofFloat(null, "scaleX", 1.0f, 1.8f)
        val scaleY = ObjectAnimator.ofFloat(null, "scaleY", 1.0f, 1.8f)
        val animatorOut = AnimatorSet()
        animatorOut.play(alphaAnima).with(scaleX).with(scaleY)
        return animatorOut
    }

    private fun createAnimatorIn(): Animator {
        val animatorIn = createAnimatorOut()
        animatorIn.interpolator = ReverseInterpolator()
        return animatorIn
    }

    fun setViewPager(viewPager: ViewPager?) {
        mViewpager = viewPager
        if (mViewpager != null && mViewpager!!.adapter != null) {
            mLastPosition = -1
            createIndicators()
            mViewpager!!.removeOnPageChangeListener(mInternalPageChangeListener)
            mViewpager!!.addOnPageChangeListener(mInternalPageChangeListener)
            mInternalPageChangeListener.onPageSelected(mViewpager!!.currentItem)
        }
    }

    @Deprecated("User ViewPager addOnPageChangeListener")
    fun setOnPageChangeListener(onPageChangeListener: OnPageChangeListener?) {
        if (mViewpager == null) {
            throw NullPointerException("can not find Viewpager , setViewPager first")
        }
        mViewpager!!.removeOnPageChangeListener(onPageChangeListener!!)
        mViewpager!!.addOnPageChangeListener(onPageChangeListener)
    }

    private fun createIndicators() {
        removeAllViews()
        if (mViewpager!!.adapter == null) {
            return
        }
        val count = mViewpager!!.adapter!!.count
        if (count <= 0) {
            return
        }
        val currentItem = mViewpager!!.currentItem
        val orientation = orientation
        for (i in 0 until count) {
            if (currentItem == i) {
                addIndicator(orientation, mImmediateAnimatorOut)
            } else {
                addIndicator(orientation, mImmediateAnimatorIn)
            }
        }
    }

    private fun addIndicator(orientation: Int, animator: Animator?) {
        if (animator!!.isRunning) {
            animator.end()
            animator.cancel()
        }
        val indicator = View(context)
        indicator.background = mIndicatorBackground
        addView(indicator, mIndicatorWidth, mIndicatorHeight)
        val lp = indicator.layoutParams as LayoutParams
        if (orientation == HORIZONTAL) {
            lp.leftMargin = mIndicatorMargin
            lp.rightMargin = mIndicatorMargin
        } else {
            lp.topMargin = mIndicatorMargin
            lp.bottomMargin = mIndicatorMargin
        }
        indicator.layoutParams = lp
        animator.setTarget(indicator)
        animator.start()
    }

    private class ReverseInterpolator : Interpolator {
        override fun getInterpolation(value: Float): Float {
            return abs(1.0f - value)
        }
    }

    companion object {
        private const val DEFAULT_INDICATOR_WIDTH = 5
    }

    init {
        init(context, attrs)
    }
}