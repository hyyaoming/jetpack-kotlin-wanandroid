package org.lym.wanandroid_kotlin.weight.preview.style

import android.widget.FrameLayout

/**
 * 图片加载进度组件接口，实现 IProgressIndicator 可扩展自己的图片加载进度组件
 */
interface IProgressIndicator {
    /**
     * 在父容器上附加一个图片加载进度 UI 控件
     *
     * @param position 当前图片的索引
     * @param parent   父容器
     */
    fun attach(position: Int, parent: FrameLayout)

    /**
     * 图片加载进度 UI 控件初始化
     *
     * @param position 索引下标
     */
    fun onStart(position: Int)

    /**
     * 图片加载进度 UI 控件显示对应的进度
     *
     * @param position 索引下标
     * @param progress 进度值(0 - 100)
     */
    fun onProgress(position: Int, progress: Int)

    /**
     * 隐藏 position 索引位置的图片加载进度 UI 控件
     *
     * @param position 索引下标
     */
    fun hideView(position: Int)

    /**
     * 图片加载完成, 移除图片加载进度 UI 控件
     *
     * @param position 索引下标
     */
    fun onFinish(position: Int)
}