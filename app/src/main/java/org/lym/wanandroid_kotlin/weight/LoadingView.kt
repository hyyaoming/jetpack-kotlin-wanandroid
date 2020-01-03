package org.lym.wanandroid_kotlin.weight

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import android.view.animation.LinearInterpolator
import org.lym.wanandroid_kotlin.R
import org.lym.wanandroid_kotlin.utils.dip2px

/**
 * author: liyaoming
 * email: liyaoming@bixin.cn
 * date: 2020-01-03-20:35
 */
class LoadingView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    private var mSize: Int = 0
    private var mPaintColor: Int = 0
    private var mAnimateValue = 0
    private var mAnimator: ValueAnimator? = null
    private val mPaint: Paint by lazy {
        Paint().apply {
            color = mPaintColor
            isAntiAlias = true
            strokeCap = Paint.Cap.ROUND
        }
    }
    private var mAutoAnimation: Boolean = false


    init {
        val array =
            getContext().obtainStyledAttributes(attrs, R.styleable.LoadingView, defStyleAttr, 0)
        mSize = array.getDimensionPixelSize(
            R.styleable.LoadingView_loading_view_size,
            dip2px(LOADING_SIZE)
        )
        mPaintColor = array.getInt(R.styleable.LoadingView_android_color, Color.WHITE)
        mAutoAnimation = array.getBoolean(R.styleable.LoadingView_auto_animation, true)
        array.recycle()
    }

    fun setColor(color: Int) {
        mPaintColor = color
        mPaint.color = color
        invalidate()
    }

    fun setSize(size: Int) {
        mSize = size
        requestLayout()
    }

    private val mUpdateListener =
        ValueAnimator.AnimatorUpdateListener { animation ->
            mAnimateValue = animation.animatedValue as Int
            invalidate()
        }

    private fun start() {
        if (mAnimator == null) {
            mAnimator = ValueAnimator.ofInt(0, LINE_COUNT - 1)
            mAnimator?.addUpdateListener(mUpdateListener)
            mAnimator?.duration = ANIM_TIME.toLong()
            mAnimator?.repeatMode = ValueAnimator.RESTART
            mAnimator?.repeatCount = ValueAnimator.INFINITE
            mAnimator?.interpolator = LinearInterpolator()
            mAnimator?.start()
        } else if (!mAnimator?.isStarted!!) {
            mAnimator?.start()
        }
    }

    private fun stop() {
        if (mAnimator != null) {
            mAnimator?.removeUpdateListener(mUpdateListener)
            mAnimator?.removeAllUpdateListeners()
            if (mAnimator?.isRunning!!)
                mAnimator?.cancel()
            mAnimator = null
        }
    }

    private fun drawLoading(canvas: Canvas, rotateDegrees: Int) {
        val width = mSize / 12
        val height = mSize / 6
        mPaint.strokeWidth = width.toFloat()
        canvas.rotate(rotateDegrees.toFloat(), (mSize / 2).toFloat(), (mSize / 2).toFloat())
        canvas.translate((mSize / 2).toFloat(), (mSize / 2).toFloat())
        for (i in 0 until LINE_COUNT) {
            canvas.rotate(DEGREE_PER_LINE.toFloat())
            mPaint.alpha = (255f * (i + 1) / LINE_COUNT).toInt()
            canvas.translate(0f, (-mSize / 2 + width / 2).toFloat())
            canvas.drawLine(0f, 0f, 0f, height.toFloat(), mPaint)
            canvas.translate(0f, (mSize / 2 - width / 2).toFloat())
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        setMeasuredDimension(mSize, mSize)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val saveCount =
            canvas.saveLayer(0f, 0f, width.toFloat(), height.toFloat(), null, Canvas.ALL_SAVE_FLAG)
        drawLoading(canvas, mAnimateValue * DEGREE_PER_LINE)
        canvas.restoreToCount(saveCount)
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        if (mAutoAnimation) {
            start()
        }
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        stop()
    }

    override fun onVisibilityChanged(changedView: View, visibility: Int) {
        super.onVisibilityChanged(changedView, visibility)
        if (visibility == VISIBLE) {
            if (mAutoAnimation) {
                start()
            }
        } else {
            stop()
        }
    }


    companion object {
        private const val LINE_COUNT = 12
        private const val DEGREE_PER_LINE = 360 / LINE_COUNT
        private const val LOADING_SIZE = 30f
        private const val ANIM_TIME = 600
    }
}