package org.lym.wanandroid_kotlin.weight.preview.view.image

import android.graphics.PointF
import android.graphics.RectF
import android.widget.ImageView.ScaleType

class PhotoInfo(
    rect: RectF?,
    img: RectF?,
    widget: RectF?,
    base: RectF?,
    screenCenter: PointF?,
    scale: Float,
    degrees: Float,
    scaleType: ScaleType
) {
    // 内部图片在整个手机界面的位置
    @JvmField
    var mRect = RectF()
    // 控件在窗口的位置
    @JvmField
    var mImgRect = RectF()
    @JvmField
    var mWidgetRect = RectF()
    var mBaseRect = RectF()
    var mScreenCenter = PointF()
    var mScale: Float
    @JvmField
    var mDegrees: Float
    @JvmField
    var mScaleType: ScaleType

    init {
        mRect.set(rect)
        mImgRect.set(img)
        mWidgetRect.set(widget)
        mScale = scale
        mScaleType = scaleType
        mDegrees = degrees
        mBaseRect.set(base)
        mScreenCenter.set(screenCenter)
    }
}