package org.lym.wanandroid_kotlin.weight.preview.style

import android.widget.FrameLayout
import androidx.viewpager.widget.ViewPager

/**
 * 图片索引指示器接口，实现 IIndexIndicator 可扩展自己的图片指示器组件
 */
interface IIndexIndicator {
    /**
     * 在父容器上添加一个图片索引指示器 UI 组件
     *
     * @param parent TransferImage
     */
    fun attach(parent: FrameLayout)

    /**
     * 显示图片索引指示器 UI 组件
     *
     * @param viewPager TransferImage
     */
    fun onShow(viewPager: ViewPager)

    /**
     * 隐藏图片索引指示器 UI 组件
     */
    fun onHide()

    /**
     * 移除图片索引指示器 UI 组件
     */
    fun onRemove()
}