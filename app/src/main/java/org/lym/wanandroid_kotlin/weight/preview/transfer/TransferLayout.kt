package org.lym.wanandroid_kotlin.weight.preview.transfer

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.PropertyValuesHolder
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.viewpager.widget.ViewPager
import androidx.viewpager.widget.ViewPager.OnPageChangeListener
import androidx.viewpager.widget.ViewPager.SimpleOnPageChangeListener
import org.lym.wanandroid_kotlin.weight.preview.transfer.TransferAdapter.OnInstantiateItemListener
import org.lym.wanandroid_kotlin.weight.preview.view.image.TransferImage
import org.lym.wanandroid_kotlin.weight.preview.view.image.TransferImage.OnTransferListener
import java.util.*
import kotlin.math.roundToInt

/**
 * Transferee 中 Dialog 显示的内容
 *
 *
 * 所有过渡动画的展示，图片的加载都是在这个 FrameLayout 中实现
 *
 *
 * Created by Hitomis on 2017/4/23 0023.
 *
 *
 * email: 196425254@qq.com
 */
class TransferLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : FrameLayout(context, attrs, defStyle) {
    private var transImage: TransferImage? = null
    lateinit var transConfig: TransferConfig
        private set
    private var dragCloseGesture: DragCloseGesture? = null
    private var layoutResetListener: OnLayoutResetListener? = null
    private val loadedIndexSet: MutableSet<Int>
    lateinit var transAdapter: TransferAdapter
    lateinit var transViewPager: ViewPager
    var bgAlpha: Float = 0f // [0.f , 255.f] = 0f

    /**
     * ViewPager 页面切换监听器 => 当页面切换时，根据相邻优先加载的规则去加载图片
     */
    private val transChangeListener: OnPageChangeListener = object : SimpleOnPageChangeListener() {
        override fun onPageSelected(position: Int) {
            transConfig.nowThumbnailIndex = position
            if (transConfig.isJustLoadHitImage) {
                loadSourceImageOffset(position, 0)
            } else {
                for (i in 1..transConfig.offscreenPageLimit) {
                    loadSourceImageOffset(position, i)
                }
            }
        }
    }
    /**
     * TransferAdapter 中对应页面创建完成监听器
     */
    private val instantListener: OnInstantiateItemListener = object : OnInstantiateItemListener {
        override fun onComplete() {
            transViewPager.addOnPageChangeListener(transChangeListener)
            val position = transConfig.nowThumbnailIndex
            if (transConfig.isJustLoadHitImage) {
                loadSourceImageOffset(position, 0)
            } else {
                loadSourceImageOffset(position, 1)
            }
        }
    }
    /**
     * TransferImage 伸/缩动画执行完成监听器
     */
    var transListener: OnTransferListener = object : OnTransferListener {
        override fun onTransferStart(state: Int, cate: Int, stage: Int) {}
        override fun onTransferUpdate(state: Int, fraction: Float) {
            val bgAlpha = if (state == TransferImage.STATE_TRANS_SPEC_OUT) {
                bgAlpha * fraction
            } else {
                255 * fraction
            }
            setBackgroundColor(getBackgroundColorByAlpha(bgAlpha))
        }

        override fun onTransferComplete(state: Int, cate: Int, stage: Int) {
            if (cate == TransferImage.CATE_ANIMA_TOGETHER) {
                when (state) {
                    TransferImage.STATE_TRANS_IN -> {
                        addIndexIndicator()
                        transViewPager.visibility = View.VISIBLE
                        removeFromParent(transImage)
                    }
                    TransferImage.STATE_TRANS_OUT, TransferImage.STATE_TRANS_SPEC_OUT -> resetTransfer()
                }
            } else { // 如果动画是分离的
                when (state) {
                    TransferImage.STATE_TRANS_IN -> if (stage == TransferImage.STAGE_TRANSLATE) { // 第一阶段位移动画执行完毕
                        addIndexIndicator()
                        transViewPager.visibility = View.VISIBLE
                        removeFromParent(transImage)
                    }
                    TransferImage.STATE_TRANS_OUT -> if (stage == TransferImage.STAGE_TRANSLATE) { // 位移动画执行完毕
                        resetTransfer()
                    }
                }
            }
        }
    }

    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        if (ev.pointerCount == 1) {
            if (dragCloseGesture != null && dragCloseGesture!!.onInterceptTouchEvent(ev)) {
                return true
            }
        }
        return super.onInterceptTouchEvent(ev)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (dragCloseGesture != null) {
            dragCloseGesture!!.onTouchEvent(event)
        }
        return super.onTouchEvent(event)
    }

    /**
     * 获取带透明度的颜色值
     *
     * @param bgAlpha [1, 255]
     * @return color int value
     */
    fun getBackgroundColorByAlpha(bgAlpha: Float): Int {
        val bgColor = transConfig.backgroundColor
        return Color.argb(
            bgAlpha.roundToInt(),
            Color.red(bgColor),
            Color.green(bgColor),
            Color.blue(bgColor)
        )
    }

    /**
     * 加载 [position - offset] 到 [position + offset] 范围内有效索引位置的图片
     *
     * @param position 当前显示图片的索引
     * @param offset   postion 左右便宜量
     */
    private fun loadSourceImageOffset(position: Int, offset: Int) {
        val left = position - offset
        val right = position + offset
        if (!loadedIndexSet.contains(position)) {
            loadSourceImage(position)
            loadedIndexSet.add(position)
        }
        if (left >= 0 && !loadedIndexSet.contains(left)) {
            loadSourceImage(left)
            loadedIndexSet.add(left)
        }
        if (right < transConfig.sourceImageList.size && !loadedIndexSet.contains(right)) {
            loadSourceImage(right)
            loadedIndexSet.add(right)
        }
    }

    /**
     * 加载索引位置为 position 处的图片
     *
     * @param position 当前有效的索引
     */
    private fun loadSourceImage(position: Int) {
        getTransferState(position).transferLoad(position)
    }

    /**
     * 重置 TransferLayout 布局中的内容
     */
    private fun resetTransfer() {
        loadedIndexSet.clear()
        removeIndexIndicator()
        removeAllViews()
        layoutResetListener!!.onReset()
    }

    /**
     * 创建 ViewPager 并添加到 TransferLayout 中
     */
    private fun createTransferViewPager() {
        transAdapter = TransferAdapter(
            this,
            transConfig.sourceImageList.size,
            transConfig.nowThumbnailIndex
        )
        transAdapter.setOnInstantListener(instantListener)
        transViewPager = ViewPager(context)
        // 先隐藏，待 ViewPager 下标为 config.getCurrOriginIndex() 的页面创建完毕再显示
        transViewPager.visibility = View.INVISIBLE
        transViewPager.offscreenPageLimit = transConfig.offscreenPageLimit + 1
        transViewPager.adapter = transAdapter
        transViewPager.currentItem = transConfig.nowThumbnailIndex
        addView(
            transViewPager,
            LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        )
    }

    /**
     * 将 view 从 view 的父布局中移除
     *
     * @param view 待移除的 view
     */
    private fun removeFromParent(view: View?) {
        val vg = view!!.parent as ViewGroup
        vg.removeView(view)
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        // unregister PageChangeListener
        transViewPager.removeOnPageChangeListener(transChangeListener)
    }

    val currentImage: TransferImage
        get() = transAdapter.getImageItem(transViewPager.currentItem)!!

    /**
     * 初始化 TransferLayout 中的各个组件，并执行图片从缩略图到 Transferee 进入动画
     */
    fun show() {
        createTransferViewPager()
        val nowThumbnailIndex = transConfig.nowThumbnailIndex
        val transferState = getTransferState(nowThumbnailIndex)
        transImage = transferState.createTransferIn(nowThumbnailIndex)
    }

    /**
     * 依据当前有效索引 position 创建并返回一个 [TransferState]
     *
     * @param position 前有效索引
     * @return [TransferState]
     */
    fun getTransferState(position: Int): TransferState {
        val transferState: TransferState
        transferState = if (!transConfig.isThumbnailEmpty) { // 客户端指定了缩略图路径集合
            RemoteThumbState(this)
        } else {
            val url = transConfig.sourceImageList[position]
            // 即使是网络图片，但是之前已经加载到本地，那么也是本地图片
            if (transConfig.imageLoader.getCache(url) != null) {
                LocalThumbState(this)
            } else {
                EmptyThumbState(this)
            }
        }
        return transferState
    }

    /**
     * 为加载完的成图片ImageView 绑定点 Transferee 操作事件
     *
     * @param imageView 加载完成的 ImageView
     * @param pos       关闭 Transferee 时图片所在的索引
     */
    fun bindOnOperationListener(
        imageView: ImageView?,
        imageUri: String?,
        pos: Int
    ) { // bind click dismiss listener
        imageView?.setOnClickListener { dismiss(pos) }
        // bind long click listener
        transConfig.longClickListener?.let { v ->
            imageView?.setOnLongClickListener {
                v.onLongClick(imageView, imageUri, pos)
                false
            }
        }
    }

    /**
     * 开启 Transferee 关闭动画，并隐藏 transferLayout 中的各个组件
     *
     * @param pos 关闭 Transferee 时图片所在的索引
     */
    fun dismiss(pos: Int) {
        if (transImage != null && transImage!!.getState()
            == TransferImage.STATE_TRANS_OUT
        ) // 防止双击
            return
        transImage = getTransferState(pos).transferOut(pos)
        if (transImage == null) diffusionTransfer(pos) else transViewPager.visibility =
            View.INVISIBLE
        hideIndexIndicator()
    }

    /**
     * 扩散消失动画
     *
     * @param pos 动画作用于 pos 索引位置的图片
     */
    fun diffusionTransfer(pos: Int) {
        transImage = transAdapter.getImageItem(pos)
        transImage!!.setState(TransferImage.STATE_TRANS_OUT)
        transImage!!.disable()
        val valueAnimator = ValueAnimator()
        valueAnimator.duration = transConfig.duration
        valueAnimator.interpolator = AccelerateDecelerateInterpolator()
        val alphaHolder = PropertyValuesHolder.ofFloat("alpha", bgAlpha, 0f)
        val scaleXHolder = PropertyValuesHolder.ofFloat("scaleX", 1f, 1.2f)
        valueAnimator.setValues(alphaHolder, scaleXHolder)
        valueAnimator.addUpdateListener { animation ->
            val alpha = animation.getAnimatedValue("alpha") as Float
            val scale = animation.getAnimatedValue("scaleX") as Float
            setBackgroundColor(getBackgroundColorByAlpha(alpha))
            transImage!!.alpha = alpha / 255f
            transImage!!.scaleX = scale
            transImage!!.scaleY = scale
        }
        valueAnimator.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                resetTransfer()
            }
        })
        valueAnimator.start()
    }

    /**
     * 配置参数
     *
     * @param config 参数对象
     */
    fun apply(config: TransferConfig) {
        transConfig = config
        if (transConfig.isEnableDragClose) dragCloseGesture = DragCloseGesture(this)
    }

    /**
     * 绑定 TransferLayout 内容重置时回调监听器
     *
     * @param listener 重置回调监听器
     */
    fun setOnLayoutResetListener(listener: OnLayoutResetListener?) {
        layoutResetListener = listener
    }

    /**
     * 在 TransferImage 面板中添加下标指示器 UI 组件
     */
    private fun addIndexIndicator() {
        val indexIndicator = transConfig.indexIndicator
        if (indexIndicator != null && transConfig.sourceImageList.size >= 2) {
            indexIndicator.attach(this)
            indexIndicator.onShow(transViewPager)
        }
    }

    /**
     * 隐藏下标指示器 UI 组件
     */
    private fun hideIndexIndicator() {
        val indexIndicator = transConfig.indexIndicator
        if (indexIndicator != null && transConfig.sourceImageList.size >= 2) {
            indexIndicator.onHide()
        }
    }

    /**
     * 从 TransferImage 面板中移除下标指示器 UI 组件
     */
    private fun removeIndexIndicator() {
        val indexIndicator = transConfig.indexIndicator
        if (indexIndicator != null && transConfig.sourceImageList.size >= 2) {
            indexIndicator.onRemove()
        }
    }

    /**
     * TransferLayout 中内容重置时监听器
     */
    interface OnLayoutResetListener {
        /**
         * 调用于：当关闭动画执行完毕，TransferLayout 中所有内容已经重置（清空）时
         */
        fun onReset()
    }

    init {
        loadedIndexSet = HashSet()
    }
}