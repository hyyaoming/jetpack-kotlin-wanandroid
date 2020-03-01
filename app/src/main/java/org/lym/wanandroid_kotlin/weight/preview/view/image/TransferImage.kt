package org.lym.wanandroid_kotlin.weight.preview.view.image

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.PropertyValuesHolder
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.graphics.drawable.Drawable
import android.view.animation.AccelerateDecelerateInterpolator

/**
 * TransferImage 主要功能：<br></br>
 *
 *
 * 可以完成从缩略图平滑伸展到一张完整的图片<br></br>
 * 也可以从整图平滑收缩到一张缩略图
 *
 *  * 支持动画：从缩略图平滑伸展到一张完整的图片
 *  * 支持动画：从整图平滑收缩到一张缩略图
 *  * 支持按指定尺寸参数裁剪后，在裁剪的区域显示图片
 *  * 支持动画分离：只有图片平移动画或者只有图片缩放动画
 *
 */
class TransferImage constructor(
    context: Context
) : PhotoView(context) {
    private var state = STATE_TRANS_NORMAL // 当前动画状态
    private var cate = CATE_ANIMA_TOGETHER // 当前动画类型
    private var stage = STAGE_TRANSLATE // 针对 CATE_ANIMA_APART 类型对话而言：当前动画的阶段
    private var originalWidth = 0
    private var originalHeight = 0
    private var originalLocationX = 0
    private var originalLocationY = 0
    private var transformStart = false // 开始动画的标记
    private lateinit var paint: Paint
    private var transMatrix: Matrix? = null
    private lateinit var specSizeF: RectF
    private var specScale = 0f
    private lateinit var transform: Transform
    var duration = DURATION

    private var transformListener: OnTransferListener? = null
    private fun init() {
        transMatrix = Matrix()
        paint = Paint()
        paint.alpha = 0
    }

    /**
     * 设置 TransferImage 初始位置信息
     *
     * @param locationX x坐标位置
     * @param locationY y坐标位置
     * @param width     宽度
     * @param height    高度
     */
    fun setOriginalInfo(locationX: Int, locationY: Int, width: Int, height: Int) {
        originalLocationX = locationX
        originalLocationY = locationY
        originalWidth = width
        originalHeight = height
    }

    /**
     * 设置 TransferImage 初始位置信息
     *
     * @param targetDrawable 初始显示的图片 Drawable
     * @param originWidth    TransferImage 初始宽度
     * @param originHeight   TransferImage 初始高度
     * @param width          容器宽度
     * @param height         容器高度
     */
    fun setOriginalInfo(
        targetDrawable: Drawable,
        originWidth: Int,
        originHeight: Int,
        width: Int,
        height: Int
    ) {
        val rect =
            getClipOriginalInfo(targetDrawable, originWidth, originHeight, width, height)
        originalLocationX = rect.left
        originalLocationY = rect.top
        originalWidth = rect.right
        originalHeight = rect.bottom
    }

    private fun getClipOriginalInfo(
        targetDrawable: Drawable,
        originWidth: Int,
        originHeight: Int,
        width: Int,
        height: Int
    ): Rect {
        val rect = Rect()
        val xSScale =
            originWidth / targetDrawable.intrinsicWidth.toFloat()
        val ySScale =
            originHeight / targetDrawable.intrinsicHeight.toFloat()
        val endScale = xSScale.coerceAtLeast(ySScale)
        val drawableEndWidth = targetDrawable.intrinsicWidth * endScale
        val drawableEndHeight = targetDrawable.intrinsicHeight * endScale
        rect.left = ((width - drawableEndWidth) / 2).toInt()
        rect.top = ((height - drawableEndHeight) / 2).toInt()
        rect.right = drawableEndWidth.toInt()
        rect.bottom = drawableEndHeight.toInt()
        return rect
    }

    /**
     * 按 [.setOriginalInfo] 方法指定的的参数裁剪显示的图片
     */
    fun transClip() {
        state = STATE_TRANS_CLIP
        transformStart = true
    }

    /**
     * 用于开始进入的方法。 调用此方前，需已经调用过setOriginalInfo
     */
    fun transformIn() {
        cate = CATE_ANIMA_TOGETHER
        state = STATE_TRANS_IN
        transformStart = true
        invalidate()
    }

    /**
     * 用于开始进入的方法(平移和放大动画分离)。 调用此方前，需已经调用过setOriginalInfo
     *
     * @param animaStage 动画阶段 :[.STAGE_TRANSLATE] 平移，[.STAGE_SCALE]
     */
    fun transformIn(animaStage: Int) {
        cate = CATE_ANIMA_APART
        state = STATE_TRANS_IN
        stage = animaStage
        transformStart = true
        invalidate()
    }

    /**
     * 用于开始退出的方法。 调用此方前，需已经调用过setOriginalInfo
     */
    fun transformOut() {
        cate = CATE_ANIMA_TOGETHER
        state = STATE_TRANS_OUT
        transformStart = true
        invalidate()
    }

    fun transformSpecOut(specSizeF: RectF, scale: Float) {
        cate = CATE_ANIMA_TOGETHER
        state = STATE_TRANS_SPEC_OUT
        transformStart = true
        this.specSizeF = specSizeF
        specScale = scale
        invalidate()
    }

    /**
     * 用于开始退出的方法(平移和放大动画分离)。 调用此方前，需已经调用过setOriginalInfo
     *
     * @param animaStage 动画阶段 :[.STAGE_TRANSLATE] 平移，[.STAGE_SCALE]
     */
    fun transformOut(animaStage: Int) {
        cate = CATE_ANIMA_APART
        state = STATE_TRANS_OUT
        stage = animaStage
        transformStart = true
        invalidate()
    }

    /**
     * 获取当前的状态
     *
     * @return [.STATE_TRANS_NORMAL], [.STATE_TRANS_IN], [.STATE_TRANS_OUT], [.STATE_TRANS_CLIP]
     */
    fun getState(): Int {
        return state
    }

    /**
     * 设置当前动画的状态
     *
     * @param state [.STATE_TRANS_NORMAL], [.STATE_TRANS_IN], [.STATE_TRANS_OUT], [.STATE_TRANS_CLIP]
     */
    fun setState(state: Int) {
        this.state = state
    }

    /**
     * 获取图片转变完全体之后的实际宽度
     *
     * @return 完全体宽度
     */
    val deformedWidth: Float
        get() {
            val transDrawable = drawable ?: return 0f
            val xEScale = width / transDrawable.intrinsicWidth.toFloat()
            val yEScale = height / transDrawable.intrinsicHeight.toFloat()
            return transDrawable.intrinsicWidth * xEScale.coerceAtMost(yEScale)
        }

    /**
     * 获取图片转变完全体之后的实际高度
     *
     * @return 完全体高度
     */
    val deformedHeight: Float
        get() {
            val transDrawable = drawable ?: return 0f
            val xEScale = width / transDrawable.intrinsicWidth.toFloat()
            val yEScale = height / transDrawable.intrinsicHeight.toFloat()
            return transDrawable.intrinsicHeight * xEScale.coerceAtMost(yEScale)
        }

    /**
     * 初始化进入的变量信息
     */
    private fun initTransform() {
        val transDrawable = drawable ?: return
        if (width == 0 || height == 0) {
            return
        }
        transform = Transform()
        /* 下面为缩放的计算 */ /* 计算初始的缩放值，初始值因为是CENTR_CROP效果，所以要保证图片的宽和高至少1个能匹配原始的宽和高，另1个大于 */
        val xSScale =
            originalWidth / transDrawable.intrinsicWidth.toFloat()
        val ySScale =
            originalHeight / transDrawable.intrinsicHeight.toFloat()
        val startScale = xSScale.coerceAtLeast(ySScale)
        transform.startScale = startScale
        /* 计算结束时候的缩放值，结束值因为要达到FIT_CENTER效果，所以要保证图片的宽和高至少1个能匹配原始的宽和高，另1个小于 */
        val xEScale = width / transDrawable.intrinsicWidth.toFloat()
        val yEScale =
            height / transDrawable.intrinsicHeight.toFloat()
        var endScale = xEScale.coerceAtMost(yEScale)
        endScale =
            if (state == STATE_TRANS_SPEC_OUT) endScale * specScale else endScale
        if (cate == CATE_ANIMA_APART && stage == STAGE_TRANSLATE) { // 平移阶段的动画，不缩放
            transform.endScale = startScale
        } else {
            transform.endScale = endScale
        }
        /*
         * 计算Canvas Clip的范围，也就是图片的显示的范围，因为图片是慢慢变大，并且是等比例的，所以这个效果还需要裁减图片显示的区域
         * ，而显示区域的变化范围是在原始CENTER_CROP效果的范围区域
         * ，到最终的FIT_CENTER的范围之间的，区域我用LocationSizeF更好计算
         * ，他就包括左上顶点坐标，和宽高，最后转为Canvas裁减的Rect.
         */
/* 开始区域 */transform.startRect = LocationSizeF()
        transform.startRect.left = originalLocationX.toFloat()
        transform.startRect.top = originalLocationY.toFloat()
        transform.startRect.width = originalWidth.toFloat()
        transform.startRect.height = originalHeight.toFloat()
        /* 结束区域 */transform.endRect = LocationSizeF()
        val bitmapEndWidth =
            transDrawable.intrinsicWidth * transform.endScale // 图片最终的宽度
        val bitmapEndHeight =
            transDrawable.intrinsicHeight * transform.endScale // 图片最终的高度
        if (state == STATE_TRANS_SPEC_OUT) {
            transform.endRect.left = specSizeF.left
            transform.endRect.top = specSizeF.top
        } else {
            transform.endRect.left = (width - bitmapEndWidth) / 2
            transform.endRect.top = (height - bitmapEndHeight) / 2
        }
        transform.endRect.width = bitmapEndWidth
        transform.endRect.height = bitmapEndHeight
        transform.rect = LocationSizeF()
    }

    private fun calcBmpMatrix() {
        val transDrawable = drawable ?: return
        /* 下面实现了CENTER_CROP的功能 */transMatrix!!.setScale(transform.scale, transform.scale)
        transMatrix!!.postTranslate(
            -(transform.scale * transDrawable.intrinsicWidth / 2 - transform.rect.width / 2),
            -(transform.scale * transDrawable.intrinsicHeight / 2 - transform.rect.height / 2)
        )
    }

    override fun onDraw(canvas: Canvas) {
        if (drawable == null) return
        if (state != STATE_TRANS_NORMAL) {
            if (transformStart) {
                initTransform()
            }
            if (transformStart) {
                when (state) {
                    STATE_TRANS_IN -> transform.initStartIn()
                    STATE_TRANS_OUT, STATE_TRANS_SPEC_OUT -> transform.initStartOut()
                    STATE_TRANS_CLIP -> transform.initStartClip()
                }
            }
            canvas.drawPaint(paint)
            val saveCount = canvas.saveCount
            canvas.save()
            // 先得到图片在此刻的图像Matrix矩阵
            calcBmpMatrix()
            canvas.translate(transform.rect.left, transform.rect.top)
            canvas.clipRect(0f, 0f, transform.rect.width, transform.rect.height)
            canvas.concat(transMatrix)
            drawable.draw(canvas)
            canvas.restoreToCount(saveCount)
            if (transformStart && state != STATE_TRANS_CLIP) {
                transformStart = false
                when (cate) {
                    CATE_ANIMA_TOGETHER -> startTogetherTrans()
                    CATE_ANIMA_APART -> startApartTrans()
                }
            }
        } else {
            canvas.drawPaint(paint)
            super.onDraw(canvas)
        }
    }

    private fun startApartTrans() {
        val valueAnimator = ValueAnimator()
        valueAnimator.duration = DURATION
        valueAnimator.interpolator = AccelerateDecelerateInterpolator()
        if (stage == STAGE_TRANSLATE) { // 平移动画
            val leftHolder = PropertyValuesHolder.ofFloat(
                "left",
                transform.startRect.left,
                transform.endRect.left
            )
            val topHolder =
                PropertyValuesHolder.ofFloat(
                    "top",
                    transform.startRect.top,
                    transform.endRect.top
                )
            val widthHolder = PropertyValuesHolder.ofFloat(
                "width",
                transform.startRect.width,
                transform.endRect.width
            )
            val heightHolder = PropertyValuesHolder.ofFloat(
                "height",
                transform.startRect.height,
                transform.endRect.height
            )
            valueAnimator.setValues(leftHolder, topHolder, widthHolder, heightHolder)
            valueAnimator.addUpdateListener { animation ->
                transformListener?.onTransferUpdate(state, animation.animatedFraction)
                transform.rect.left = animation.getAnimatedValue("left") as Float
                transform.rect.top = animation.getAnimatedValue("top") as Float
                transform.rect.width = animation.getAnimatedValue("width") as Float
                transform.rect.height = animation.getAnimatedValue("height") as Float
                invalidate()
            }
        } else { // 缩放动画
            val leftHolder = PropertyValuesHolder.ofFloat(
                "left",
                transform.startRect.left,
                transform.endRect.left
            )
            val topHolder =
                PropertyValuesHolder.ofFloat(
                    "top",
                    transform.startRect.top,
                    transform.endRect.top
                )
            val widthHolder = PropertyValuesHolder.ofFloat(
                "width",
                transform.startRect.width,
                transform.endRect.width
            )
            val heightHolder = PropertyValuesHolder.ofFloat(
                "height",
                transform.startRect.height,
                transform.endRect.height
            )
            val scaleHolder =
                PropertyValuesHolder.ofFloat("scale", transform.startScale, transform.endScale)
            valueAnimator.setValues(scaleHolder, leftHolder, topHolder, widthHolder, heightHolder)
            valueAnimator.addUpdateListener { animation ->
                transform.rect.left = animation.getAnimatedValue("left") as Float
                transform.rect.top = animation.getAnimatedValue("top") as Float
                transform.rect.width = animation.getAnimatedValue("width") as Float
                transform.rect.height = animation.getAnimatedValue("height") as Float
                transform.scale = animation.getAnimatedValue("scale") as Float
                invalidate()
            }
        }
        valueAnimator.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationStart(animation: Animator) {
                transformListener?.onTransferStart(
                    state,
                    cate,
                    stage
                )
            }

            override fun onAnimationEnd(animation: Animator) {
                if (stage == STAGE_TRANSLATE) {
                    transform.endRect.apply {
                        originalLocationX = this.left.toInt()
                        originalLocationY = this.top.toInt()
                        originalWidth = this.width.toInt()
                        originalHeight = this.height.toInt()
                    }
                }
                if (state == STATE_TRANS_IN && stage == STAGE_SCALE) {
                    state = STATE_TRANS_NORMAL
                }
                transformListener?.onTransferComplete(
                    state,
                    cate,
                    stage
                )
            }
        })
        if (state == STATE_TRANS_IN) {
            valueAnimator.start()
        } else {
            valueAnimator.reverse()
        }
    }

    private fun startTogetherTrans() {
        val valueAnimator = ValueAnimator()
        valueAnimator.duration = DURATION
        valueAnimator.interpolator = AccelerateDecelerateInterpolator()
        val scaleHolder =
            PropertyValuesHolder.ofFloat("scale", transform.startScale, transform.endScale)
        val leftHolder =
            PropertyValuesHolder.ofFloat(
                "left",
                transform.startRect.left,
                transform.endRect.left
            )
        val topHolder =
            PropertyValuesHolder.ofFloat(
                "top",
                transform.startRect.top,
                transform.endRect.top
            )
        val widthHolder = PropertyValuesHolder.ofFloat(
            "width",
            transform.startRect.width,
            transform.endRect.width
        )
        val heightHolder = PropertyValuesHolder.ofFloat(
            "height",
            transform.startRect.height,
            transform.endRect.height
        )
        valueAnimator.setValues(scaleHolder, leftHolder, topHolder, widthHolder, heightHolder)
        valueAnimator.addUpdateListener { animation ->
            transformListener?.onTransferUpdate(state, animation.animatedFraction)
            transform.scale = animation.getAnimatedValue("scale") as Float
            transform.rect.apply {
                this.left = animation.getAnimatedValue("left") as Float
                this.top = animation.getAnimatedValue("top") as Float
                this.width = animation.getAnimatedValue("width") as Float
                this.height = animation.getAnimatedValue("height") as Float
            }
            invalidate()
        }
        valueAnimator.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationStart(animation: Animator) {
                if (transformListener != null) {
                    transformListener!!.onTransferStart(
                        state,
                        cate,
                        stage
                    )
                }
            }

            override fun onAnimationEnd(animation: Animator) {
                if (transformListener != null) {
                    transformListener!!.onTransferComplete(
                        state,
                        cate,
                        stage
                    )
                }
                /*
                 * 如果是进入的话，当然是希望最后停留在center_crop的区域。但是如果是out的话，就不应该是center_crop的位置了
                 * ， 而应该是最后变化的位置，因为当out的时候结束时，不回复视图是Normal，要不然会有一个突然闪动回去的bug
                 */
                if (state == STATE_TRANS_IN) {
                    state = STATE_TRANS_NORMAL
                }
            }
        })
        if (state == STATE_TRANS_IN) {
            valueAnimator.start()
        } else {
            valueAnimator.reverse()
        }
    }

    fun setOnTransferListener(listener: OnTransferListener?) {
        transformListener = listener
    }

    interface OnTransferListener {
        /**
         * @param state [.STATE_TRANS_IN] [.STATE_TRANS_OUT]
         * @param cate  [.CATE_ANIMA_TOGETHER] [.CATE_ANIMA_APART]
         * @param stage [.STAGE_TRANSLATE] [.STAGE_SCALE]
         */
        fun onTransferStart(state: Int, cate: Int, stage: Int)

        fun onTransferUpdate(state: Int, fraction: Float)
        /**
         * @param state [.STATE_TRANS_IN] [.STATE_TRANS_OUT]
         * @param cate  [.CATE_ANIMA_TOGETHER] [.CATE_ANIMA_APART]
         * @param stage [.STAGE_TRANSLATE] [.STAGE_SCALE]
         */
        fun onTransferComplete(state: Int, cate: Int, stage: Int)
    }

    private class Transform {
        var startScale = 0f // 图片开始的缩放值 = 0f
        var endScale = 0f // 图片结束的缩放值 = 0f
        var scale = 0f// 属性ValueAnimator计算出来的值 = 0f
        var startRect: LocationSizeF = LocationSizeF()// 开始的区域
        var endRect: LocationSizeF = LocationSizeF()// 结束的区域
        var rect: LocationSizeF = LocationSizeF()// 属性ValueAnimator计算出来的值

        fun initStartIn() {
            scale = startScale
            try {
                rect = startRect.clone() as LocationSizeF
            } catch (e: CloneNotSupportedException) {
                e.printStackTrace()
            }
        }

        fun initStartOut() {
            scale = endScale
            try {
                rect = endRect.clone() as LocationSizeF
            } catch (e: CloneNotSupportedException) {
                e.printStackTrace()
            }
        }

        fun initStartClip() {
            scale = startScale
            try {
                rect = endRect.clone() as LocationSizeF
            } catch (e: CloneNotSupportedException) {
                e.printStackTrace()
            }
        }
    }

    private class LocationSizeF : Cloneable {
        var left = 0f
        var top = 0f
        var width = 0f
        var height = 0f
        override fun toString(): String {
            return "[left:$left top:$top width:$width height:$height]"
        }

        @Throws(CloneNotSupportedException::class)
        public override fun clone(): Any {
            return super.clone()
        }
    }

    companion object {
        const val STATE_TRANS_NORMAL = 0 // 普通状态
        const val STATE_TRANS_IN = 1 // 从缩略图到大图状态
        const val STATE_TRANS_OUT = 2 // 从大图到缩略图状态
        const val STATE_TRANS_SPEC_OUT = 3 // 从大图到缩略图状态并指定起始大图状态
        const val STATE_TRANS_CLIP = 4 // 裁剪状态
        const val CATE_ANIMA_TOGETHER = 100 // 动画类型：位移和缩放同时进行
        const val CATE_ANIMA_APART = 200 // 动画类型：位移和缩放分开进行
        const val STAGE_TRANSLATE = 201 // 平移
        const val STAGE_SCALE = 202 // 缩放
        const val DURATION = 300L    //设置伸缩动画执行的时间
    }

    init {
        init()
    }
}