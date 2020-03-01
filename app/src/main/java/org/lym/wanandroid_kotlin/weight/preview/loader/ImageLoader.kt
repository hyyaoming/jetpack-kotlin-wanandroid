package org.lym.wanandroid_kotlin.weight.preview.loader

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.widget.ImageView
import androidx.annotation.UiThread
import java.io.File

/**
 * 图片预览加载器
 *
 * author: ym.li
 * since: 2020/3/1
 */
interface ImageLoader {
    /**
     * 加载并显示原图
     *
     * @param imageUrl       图片地址
     * @param imageView      用于图片加载成功后显示的 ImageView
     * @param placeholder    加载完成之前显示的占位图
     * @param sourceCallback 图片加载过程的回调
     */
    fun showImage(
        imageUrl: String?,
        imageView: ImageView?,
        placeholder: Drawable?,
        sourceCallback: SourceCallback?
    )

    /**
     * 异步加载图片
     *
     * @param imageUrl 图片地址
     * @param callback 片加载完成的回调
     */
    fun loadImageAsync(imageUrl: String?, callback: ThumbnailCallback?)

    /**
     * 从本地同步加载图片（imageUrl 对应的图片是已经加载过的），返回 Bitmap
     */
    fun loadImageSync(imageUrl: String?): Bitmap?

    /**
     * 获取 url 关联的图片缓存
     */
    fun getCache(url: String?): File?

    /**
     * 清除 ImageLoader 缓存
     */
    fun clearCache()

    interface SourceCallback {
        @UiThread
        fun onStart()

        @UiThread
        fun onProgress(progress: Int)

        @UiThread
        fun onDelivered(status: Int, source: File?)
    }

    interface ThumbnailCallback {
        @UiThread
        fun onFinish(bitmap: Bitmap?)
    }

    companion object {
        /**
         * 状态码，取消加载原图
         */
        const val STATUS_DISPLAY_CANCEL = -1
        /**
         * 状态码，加载原图失败
         */
        const val STATUS_DISPLAY_FAILED = 0
        /**
         * 状态码，加载原图成功
         */
        const val STATUS_DISPLAY_SUCCESS = 1
    }
}