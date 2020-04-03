package org.lym.wanandroid_kotlin.weight.preview.transfer

import android.animation.*
import android.annotation.SuppressLint
import android.graphics.RectF
import android.view.*
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.ImageView.ScaleType
import org.lym.wanandroid_kotlin.weight.preview.view.image.TransferImage
import kotlin.math.abs

/**
 * Created by Vans Z on 2019-11-05.
 */
internal class DragCloseGesture(private val transferLayout: TransferLayout) {
    private var velocityTracker: VelocityTracker? = null
    private var preX = 0f
    private var preY = 0f
    private var scale = 0f // 拖拽图片缩放值 = 0f
    private val touchSlop: Int = ViewConfiguration.get(transferLayout.context).scaledEdgeSlop

    fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        if (ev.pointerCount == 1) {
            when (ev.action) {
                MotionEvent.ACTION_DOWN -> {
                    preX = ev.rawX
                    preY = ev.rawY
                    if (null == velocityTracker) {
                        velocityTracker = VelocityTracker.obtain()
                    } else {
                        velocityTracker?.clear()
                    }
                    velocityTracker?.addMovement(ev)
                }
                MotionEvent.ACTION_MOVE -> {
                    val diffY = ev.rawY - preY
                    val diffX = abs(ev.rawX - preX)
                    val currentImage = transferLayout.currentImage
                    if (diffX < touchSlop && diffY > touchSlop && currentImage.isScrollTop) {
                        return true
                    }
                }
                MotionEvent.ACTION_UP -> preY = 0f
            }
        }
        return false
    }

    fun onTouchEvent(event: MotionEvent) {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                preX = event.rawX
                preY = event.rawY
            }
            MotionEvent.ACTION_MOVE -> {
                velocityTracker?.addMovement(event)
                val diffX = event.rawX - preX
                val diffY = event.rawY - preY
                val absDiffY = abs(diffY)
                scale = 1 - absDiffY / transferLayout.height * .75f
                if (absDiffY < 350) {
                    transferLayout.bgAlpha = 255 - absDiffY / 350 * 25
                } else {
                    transferLayout.bgAlpha =
                        230 - (absDiffY - 350) * 1.35f / transferLayout.height * 255
                }
                transferLayout.bgAlpha =
                    if (transferLayout.bgAlpha < 0) 0f else transferLayout.bgAlpha
                val transViewPager = transferLayout.transViewPager
                if (transViewPager.translationY >= 0) {
                    transferLayout.setBackgroundColor(
                        transferLayout.getBackgroundColorByAlpha(
                            transferLayout.bgAlpha
                        )
                    )
                    transViewPager.translationX = diffX
                    transViewPager.translationY = diffY
                    transViewPager.scaleX = scale
                    transViewPager.scaleY = scale
                    transferLayout.transConfig.indexIndicator?.onHide()
                } else {
                    transferLayout.setBackgroundColor(transferLayout.transConfig.backgroundColor)
                    transViewPager.translationX = diffX
                    transViewPager.translationY = diffY
                }
            }
            MotionEvent.ACTION_UP -> {
                velocityTracker?.addMovement(event)
                velocityTracker?.computeCurrentVelocity(1000)
                val velocityY = velocityTracker?.yVelocity
                if (velocityY!! > 100) {
                    val pos = transferLayout.transConfig.nowThumbnailIndex
                    val originImage =
                        transferLayout.transConfig.originImageList[pos]
                    if (originImage == null) { // 走扩散消失动画
                        transferLayout.diffusionTransfer(pos)
                    } else { // 走过渡动画
                        startTransformAnima(pos, originImage)
                    }
                } else {
                    startFlingAndRollbackAnimation()
                }
                preX = 0f
                preY = 0f
            }
            MotionEvent.ACTION_CANCEL ->
                velocityTracker?.let {
                    it.recycle()
                    velocityTracker = null
                }
        }
    }

    private fun startTransformAnima(pos: Int, originImage: ImageView) {
        val transViewPagerUp = transferLayout.transViewPager
        transViewPagerUp.visibility = View.INVISIBLE
        val location = IntArray(2)
        originImage.getLocationInWindow(location)
        val x = location[0]
        val y = location[1]
        val width = originImage.width
        val height = originImage.height
        val transImage = TransferImage(transferLayout.context)
        transImage.scaleType = ScaleType.FIT_CENTER
        transImage.setOriginalInfo(x, y, width, height)
        transImage.duration = 300
        transImage.layoutParams = FrameLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        transImage.setOnTransferListener(transferLayout.transListener)
        transImage.setImageDrawable(transferLayout.transAdapter.getImageItem(pos)?.drawable)
        val currTransImage = transferLayout.currentImage
        val realWidth = currTransImage.deformedWidth * scale
        val realHeight = currTransImage.deformedHeight * scale
        val left =
            transViewPagerUp.translationX + (transferLayout.width - realWidth) * .5f
        val top =
            transViewPagerUp.translationY + (transferLayout.height - realHeight) * .5f
        val rectF = RectF(left, top, realWidth, realHeight)
        transImage.transformSpecOut(rectF, scale)
        transferLayout.addView(transImage, 1)
    }

    @SuppressLint("ObjectAnimatorBinding")
    private fun startFlingAndRollbackAnimation() {
        val transViewPager = transferLayout.transViewPager
        val bgColor: ValueAnimator =
            ObjectAnimator.ofFloat(null, "alpha", transferLayout.bgAlpha, 255f)
        val scaleX = ObjectAnimator.ofFloat(transViewPager, "scaleX", transViewPager.scaleX, 1.0f)
        val scaleY = ObjectAnimator.ofFloat(transViewPager, "scaleY", transViewPager.scaleX, 1.0f)
        val transX =
            ObjectAnimator.ofFloat(transViewPager, "translationX", transViewPager.translationX, 0f)
        val transY =
            ObjectAnimator.ofFloat(transViewPager, "translationY", transViewPager.translationY, 0f)
        bgColor.addUpdateListener { animation: ValueAnimator ->
            val value = animation.animatedValue.toString().toFloat()
            transferLayout.setBackgroundColor(transferLayout.getBackgroundColorByAlpha(value))
        }
        val animatorSet = AnimatorSet()
        animatorSet.playTogether(bgColor, scaleX, scaleY, transX, transY)
        animatorSet.addListener(object  : AnimatorListenerAdapter(){
            override fun onAnimationEnd(animation: Animator?) {
                super.onAnimationEnd(animation)
                transferLayout.transConfig.indexIndicator?.onShow(transViewPager)
            }
        })
        animatorSet.start()
    }

}