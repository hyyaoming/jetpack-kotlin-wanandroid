package org.lym.wanandroid_kotlin.weight

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import org.lym.wanandroid_kotlin.R

/**
 * 功能类似于TagFlowLayout,但是这个支持设置显示最多行数，最多个数，对于子View点击事件支持不太友好。
 *
 * author: liyaoming
 * email: liyaoming@bixin.cn
 * date: 2020-01-03-20:35
 */
class FloatLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ViewGroup(context, attrs, defStyleAttr) {
    private var mChildHorizontalSpacing: Int = 0
    private var mChildVerticalSpacing: Int = 0
    /**
     * 对齐方式，目前支持 [Gravity.CENTER_HORIZONTAL], [Gravity.LEFT] 和 [Gravity.RIGHT]
     */
    private var mGravity: Int = 0
    private var mMaxMode = LINES
    private var mMaximum = Integer.MAX_VALUE
    private var lineCount = 0
    private var mOnLineCountChangeListener: OnLineCountChangeListener? = null
    /**
     *
     * 每一行的item数目，下标表示行下标，在onMeasured的时候计算得出，供onLayout去使用。
     *
     * 若mItemNumberInEachLine[x]==0，则表示第x行已经没有item了
     */
    private lateinit var mItemNumberInEachLine: IntArray
    /**
     *
     * 每一行的item的宽度和（包括item直接的间距），下标表示行下标，
     * 如 mWidthSumInEachLine[x]表示第x行的item的宽度和（包括item直接的间距）
     *
     * 在onMeasured的时候计算得出，供onLayout去使用
     */
    private lateinit var mWidthSumInEachLine: IntArray
    /**
     * onMeasure过程中实际参与measure的子View个数
     */
    private var measuredChildCount: Int = 0

    /**
     * 设置子 View 的对齐方式，目前支持 [Gravity.CENTER_HORIZONTAL], [Gravity.LEFT] 和 [Gravity.RIGHT]
     */
    var gravity: Int
        get() = mGravity
        set(gravity) {
            if (mGravity != gravity) {
                mGravity = gravity
                requestLayout()
            }
        }

    /**
     * 获取最多可显示的子View个数
     */
    /**
     * 设置最多可显示的子View个数
     * 注意该方法不会改变子View的个数，只会影响显示出来的子View个数
     *
     * @param maxNumber 最多可显示的子View个数
     */
    var maxNumber: Int
        get() = if (mMaxMode == NUMBER) mMaximum else -1
        set(maxNumber) {
            mMaximum = maxNumber
            mMaxMode = NUMBER
            requestLayout()
        }

    /**
     * 获取最多可显示的行数
     *
     * @return 没有限制时返回-1
     */
    /**
     * 设置最多可显示的行数
     * 注意该方法不会改变子View的个数，只会影响显示出来的子View个数
     *
     * @param maxLines 最多可显示的行数
     */
    var maxLines: Int
        get() = if (mMaxMode == LINES) mMaximum else -1
        set(maxLines) {
            mMaximum = maxLines
            mMaxMode = LINES
            requestLayout()
        }

    init {
        val array = context.obtainStyledAttributes(
            attrs,
            R.styleable.FloatLayout
        )
        mChildHorizontalSpacing = array.getDimensionPixelSize(
            R.styleable.FloatLayout_childHorizontalSpacing, 0
        )
        mChildVerticalSpacing = array.getDimensionPixelSize(
            R.styleable.FloatLayout_childVerticalSpacing, 0
        )
        mGravity = array.getInteger(R.styleable.FloatLayout_android_gravity, Gravity.START)
        val lines = array.getInt(R.styleable.FloatLayout_android_maxLines, -1)
        if (lines >= 0) {
            maxLines = lines
        }
        val number = array.getInt(R.styleable.FloatLayout_maxNumber, -1)
        if (number >= 0) {
            maxNumber = number
        }
        array.recycle()
    }

    @SuppressLint("DrawAllocation")
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val widthSpecMode = View.MeasureSpec.getMode(widthMeasureSpec)
        val widthSpecSize = View.MeasureSpec.getSize(widthMeasureSpec)
        val heightSpecMode = View.MeasureSpec.getMode(heightMeasureSpec)
        val heightSpecSize = View.MeasureSpec.getSize(heightMeasureSpec)

        var maxLineHeight = 0

        var resultWidth: Int
        var resultHeight: Int

        val count = childCount

        mItemNumberInEachLine = IntArray(count)
        mWidthSumInEachLine = IntArray(count)
        var lineIndex = 0

        // 若FloatLayout指定了MATCH_PARENT或固定宽度，则需要使子View换行
        if (widthSpecMode == View.MeasureSpec.EXACTLY) {
            resultWidth = widthSpecSize

            measuredChildCount = 0

            // 下一个子View的position
            var childPositionX = paddingLeft
            var childPositionY = paddingTop

            // 子View的Right最大可达到的x坐标
            val childMaxRight = widthSpecSize - paddingRight

            for (i in 0 until count) {
                if (mMaxMode == NUMBER && measuredChildCount >= mMaximum) {
                    // 超出最多数量，则不再继续
                    break
                } else if (mMaxMode == LINES && lineIndex >= mMaximum) {
                    // 超出最多行数，则不再继续
                    break
                }

                val child = getChildAt(i)
                if (child.visibility == View.GONE) {
                    continue
                }

                val childLayoutParams = child.layoutParams
                val childWidthMeasureSpec = ViewGroup.getChildMeasureSpec(
                    widthMeasureSpec,
                    paddingLeft + paddingRight, childLayoutParams.width
                )
                val childHeightMeasureSpec = ViewGroup.getChildMeasureSpec(
                    heightMeasureSpec,
                    paddingTop + paddingBottom, childLayoutParams.height
                )
                child.measure(childWidthMeasureSpec, childHeightMeasureSpec)

                val childw = child.measuredWidth
                maxLineHeight = Math.max(maxLineHeight, child.measuredHeight)
                // 需要换行
                if (childPositionX + childw > childMaxRight) {
                    // 如果换行后超出最大行数，则不再继续
                    if (mMaxMode == LINES) {
                        if (lineIndex + 1 >= mMaximum) {
                            break
                        }
                    }
                    mWidthSumInEachLine[lineIndex] -= mChildHorizontalSpacing // 后面每次加item都会加上一个space，这样的话每行都会为最后一个item多加一次space，所以在这里减一次
                    lineIndex++ // 换行
                    childPositionX = paddingLeft // 下一行第一个item的x
                    childPositionY += maxLineHeight + mChildVerticalSpacing // 下一行第一个item的y
                }
                if (lineIndex >= mItemNumberInEachLine.size) {
                    lineIndex = mItemNumberInEachLine.size - 1
                }
                mItemNumberInEachLine[lineIndex]++
                mWidthSumInEachLine[lineIndex] += childw + mChildHorizontalSpacing
                childPositionX += childw + mChildHorizontalSpacing
                measuredChildCount++
            }
            // 如果最后一个item不是刚好在行末（即lineCount最后没有+1，也就是mWidthSumInEachLine[lineCount]非0），则要减去最后一个item的space
            if (mWidthSumInEachLine.size > 0 && mWidthSumInEachLine[lineIndex] > 0) {
                mWidthSumInEachLine[lineIndex] -= mChildHorizontalSpacing
            }
            if (heightSpecMode == View.MeasureSpec.UNSPECIFIED) {
                resultHeight = childPositionY + maxLineHeight + paddingBottom
            } else if (heightSpecMode == View.MeasureSpec.AT_MOST) {
                resultHeight = childPositionY + maxLineHeight + paddingBottom
                resultHeight = Math.min(resultHeight, heightSpecSize)
            } else {
                resultHeight = heightSpecSize
            }
        } else {
            // 不计算换行，直接一行铺开
            resultWidth = paddingLeft + paddingRight
            measuredChildCount = 0

            for (i in 0 until count) {
                if (mMaxMode == NUMBER) {
                    // 超出最多数量，则不再继续
                    if (measuredChildCount > mMaximum) {
                        break
                    }
                } else if (mMaxMode == LINES) {
                    // 超出最大行数，则不再继续
                    if (1 > mMaximum) {
                        break
                    }
                }
                val child = getChildAt(i)
                if (child.visibility == View.GONE) {
                    continue
                }
                val childLayoutParams = child.layoutParams
                val childWidthMeasureSpec = ViewGroup.getChildMeasureSpec(
                    widthMeasureSpec,
                    paddingLeft + paddingRight, childLayoutParams.width
                )
                val childHeightMeasureSpec = ViewGroup.getChildMeasureSpec(
                    heightMeasureSpec,
                    paddingTop + paddingBottom, childLayoutParams.height
                )
                child.measure(childWidthMeasureSpec, childHeightMeasureSpec)
                resultWidth += child.measuredWidth
                maxLineHeight = Math.max(maxLineHeight, child.measuredHeight)
                measuredChildCount++
            }
            if (measuredChildCount > 0) {
                resultWidth += mChildHorizontalSpacing * (measuredChildCount - 1)
            }
            resultHeight = maxLineHeight + paddingTop + paddingBottom
            if (mItemNumberInEachLine.isNotEmpty()) {
                mItemNumberInEachLine[lineIndex] = count
            }
            if (mWidthSumInEachLine.isNotEmpty()) {
                mWidthSumInEachLine[0] = resultWidth
            }
        }
        setMeasuredDimension(resultWidth, resultHeight)
        val meausureLineCount = lineIndex + 1
        if (lineCount != meausureLineCount) {
            mOnLineCountChangeListener?.onChange(lineCount, measuredChildCount)
            lineCount = meausureLineCount
        }
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        val width = right - left
        // 按照不同gravity使用不同的布局，默认是left
        when (mGravity and Gravity.HORIZONTAL_GRAVITY_MASK) {
            Gravity.START -> layoutWithGravityLeft(width)
            Gravity.END -> layoutWithGravityRight(width)
            Gravity.CENTER_HORIZONTAL -> layoutWithGravityCenterHorizontal(width)
            else -> layoutWithGravityLeft(width)
        }
    }

    /**
     * 将子View靠左布局
     */
    private fun layoutWithGravityLeft(parentWidth: Int) {
        val childMaxRight = parentWidth - paddingRight
        var childPositionX = paddingLeft
        var childPositionY = paddingTop
        var lineHeight = 0
        val childCount = childCount
        val childCountToLayout = Math.min(childCount, measuredChildCount)
        for (i in 0 until childCountToLayout) {
            val child = getChildAt(i)
            if (child.visibility == View.GONE) {
                continue
            }
            val childw = child.measuredWidth
            val childh = child.measuredHeight
            if (childPositionX + childw > childMaxRight) {
                // 换行
                childPositionX = paddingLeft
                childPositionY += lineHeight + mChildVerticalSpacing
                lineHeight = 0
            }
            child.layout(
                childPositionX,
                childPositionY,
                childPositionX + childw,
                childPositionY + childh
            )
            childPositionX += childw + mChildHorizontalSpacing
            lineHeight = Math.max(lineHeight, childh)
        }

        // 如果布局的子View少于childCount，则表示有一些子View不需要布局
        if (measuredChildCount < childCount) {
            for (i in measuredChildCount until childCount) {
                val child = getChildAt(i)
                if (child.visibility == View.GONE) {
                    continue
                }
                child.layout(0, 0, 0, 0)
            }
        }
    }

    /**
     * 将子View靠右布局
     */
    private fun layoutWithGravityRight(parentWidth: Int) {
        var nextChildIndex = 0
        var nextChildPositionX: Int
        var nextChildPositionY = paddingTop
        var lineHeight = 0

        // 遍历每一行
        for (i in mItemNumberInEachLine.indices) {
            // 如果这一行已经没item了，则退出循环
            if (mItemNumberInEachLine[i] == 0) {
                break
            }

            if (nextChildIndex > measuredChildCount - 1) {
                break
            }

            // 遍历该行内的元素，布局每个元素
            nextChildPositionX =
                parentWidth - paddingRight - mWidthSumInEachLine[i] // 初始值为子 View 的最小 x 值
            for (j in nextChildIndex until nextChildIndex + mItemNumberInEachLine[i]) {
                val childView = getChildAt(j)
                if (childView.visibility == View.GONE) {
                    continue
                }
                val childw = childView.measuredWidth
                val childh = childView.measuredHeight
                childView.layout(
                    nextChildPositionX,
                    nextChildPositionY,
                    nextChildPositionX + childw,
                    nextChildPositionY + childh
                )
                lineHeight = Math.max(lineHeight, childh)
                nextChildPositionX += childw + mChildHorizontalSpacing
            }

            // 一行结束了，整理一下，准备下一行
            nextChildPositionY += lineHeight + mChildVerticalSpacing
            nextChildIndex += mItemNumberInEachLine[i]
            lineHeight = 0
        }

        val childCount = childCount
        if (measuredChildCount < childCount) {
            for (i in measuredChildCount until childCount) {
                val childView = getChildAt(i)
                if (childView.visibility == View.GONE) {
                    continue
                }
                childView.layout(0, 0, 0, 0)
            }
        }
    }

    /**
     * 将子View居中布局
     */
    private fun layoutWithGravityCenterHorizontal(parentWidth: Int) {
        var nextChildIndex = 0
        var nextChildPositionX: Int
        var nextChildPositionY = paddingTop
        var lineHeight = 0
        // 遍历每一行
        for (i in mItemNumberInEachLine.indices) {
            // 如果这一行已经没item了，则退出循环
            if (mItemNumberInEachLine[i] == 0) {
                break
            }

            if (nextChildIndex > measuredChildCount - 1) {
                break
            }

            // 遍历该行内的元素，布局每个元素
            nextChildPositionX =
                (parentWidth - paddingLeft - paddingRight - mWidthSumInEachLine[i]) / 2 + paddingLeft // 子 View 的最小 x 值
            for (j in nextChildIndex until nextChildIndex + mItemNumberInEachLine[i]) {
                val childView = getChildAt(j)
                if (childView.visibility == View.GONE) {
                    continue
                }
                val childw = childView.measuredWidth
                val childh = childView.measuredHeight
                childView.layout(
                    nextChildPositionX,
                    nextChildPositionY,
                    nextChildPositionX + childw,
                    nextChildPositionY + childh
                )
                lineHeight = Math.max(lineHeight, childh)
                nextChildPositionX += childw + mChildHorizontalSpacing
            }

            // 一行结束了，整理一下，准备下一行
            nextChildPositionY += lineHeight + mChildVerticalSpacing
            nextChildIndex += mItemNumberInEachLine[i]
            lineHeight = 0
        }

        val childCount = childCount
        if (measuredChildCount < childCount) {
            for (i in measuredChildCount until childCount) {
                val childView = getChildAt(i)
                if (childView.visibility == View.GONE) {
                    continue
                }
                childView.layout(0, 0, 0, 0)
            }
        }
    }

    fun setOnLineCountChangeListener(onLineCountChangeListener: OnLineCountChangeListener) {
        mOnLineCountChangeListener = onLineCountChangeListener
    }

    /**
     * 设置子 View 的水平间距
     */
    fun setChildHorizontalSpacing(spacing: Int) {
        mChildHorizontalSpacing = spacing
        invalidate()
    }

    /**
     * 设置子 View 的垂直间距
     */
    fun setChildVerticalSpacing(spacing: Int) {
        mChildVerticalSpacing = spacing
        invalidate()
    }

    interface OnLineCountChangeListener {
        fun onChange(oldLineCount: Int, newLineCount: Int)
    }

    companion object {
        private const val LINES = 0
        private const val NUMBER = 1
    }
}
