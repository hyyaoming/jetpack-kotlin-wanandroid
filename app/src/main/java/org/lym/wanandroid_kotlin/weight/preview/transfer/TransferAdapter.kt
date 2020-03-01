package org.lym.wanandroid_kotlin.weight.preview.transfer

import android.util.SparseArray
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.viewpager.widget.PagerAdapter
import org.lym.wanandroid_kotlin.weight.preview.view.image.TransferImage

/**
 * 展示大图组件 ViewPager 的图片数据适配器
 */
class TransferAdapter(
    private val transfer: TransferLayout,
    private val imageSize: Int,
    nowThumbnailIndex: Int
) : PagerAdapter() {
    private var showIndex: Int =
        if (nowThumbnailIndex + 1 == imageSize) nowThumbnailIndex - 1 else nowThumbnailIndex + 1
    private var onInstantListener: OnInstantiateItemListener? = null
    private val containLayoutArray: SparseArray<FrameLayout>
    override fun getCount(): Int {
        return imageSize
    }

    override fun isViewFromObject(
        view: View,
        `object`: Any
    ): Boolean {
        return view === `object`
    }

    override fun destroyItem(
        container: ViewGroup,
        position: Int,
        `object`: Any
    ) {
        container.removeView(`object` as View)
    }

    /**
     * 获取指定索引页面中的 TransferImage
     *
     * @param position 下标
     * @return  TransferImage
     */
    fun getImageItem(position: Int): TransferImage? {
        var transImage: TransferImage? = null
        val parentLayout = containLayoutArray[position]
        if (parentLayout != null) {
            val childCount = parentLayout.childCount
            for (i in 0 until childCount) {
                val view = parentLayout.getChildAt(i)
                if (view is ImageView) {
                    transImage = view as TransferImage
                    break
                }
            }
        }
        return transImage
    }

    fun getParentItem(position: Int): FrameLayout {
        return containLayoutArray[position]
    }

    fun setOnInstantListener(listener: OnInstantiateItemListener?) {
        onInstantListener = listener
    }

    override fun instantiateItem(
        container: ViewGroup,
        position: Int
    ): Any { // ViewPager instantiateItem 顺序：按 position 递减 OffscreenPageLimit，
// 再从 positon 递增 OffscreenPageLimit 的次序创建页面
        var parentLayout = containLayoutArray[position]
        if (parentLayout == null) {
            parentLayout = newParentLayout(container, position)
            containLayoutArray.put(position, parentLayout)
            if (position == showIndex) {
                onInstantListener?.onComplete()
            }
        }
        container.addView(parentLayout)
        return parentLayout
    }

    private fun newParentLayout(container: ViewGroup, pos: Int): FrameLayout {
        val context = container.context
        val config = transfer.transConfig
        // create inner ImageView
        val imageView = TransferImage(context)
        imageView.duration = config.duration
        imageView.scaleType = ImageView.ScaleType.FIT_CENTER
        imageView.layoutParams = FrameLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        // create outer ParentLayout
        val parentLayout = FrameLayout(context)
        parentLayout.layoutParams = FrameLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        parentLayout.addView(imageView)
        if (config.isJustLoadHitImage) transfer.getTransferState(pos).prepareTransfer(
            imageView,
            pos
        )
        return parentLayout
    }

    interface OnInstantiateItemListener {
        fun onComplete()
    }

    init {
        showIndex = showIndex.coerceAtLeast(0)
        containLayoutArray = SparseArray()
    }
}