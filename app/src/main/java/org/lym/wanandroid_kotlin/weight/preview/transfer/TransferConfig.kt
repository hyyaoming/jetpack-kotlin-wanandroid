package org.lym.wanandroid_kotlin.weight.preview.transfer

import android.graphics.Color
import android.graphics.drawable.Drawable
import android.widget.AbsListView
import android.widget.ImageView
import androidx.annotation.IdRes
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import org.lym.wanandroid_kotlin.R
import org.lym.wanandroid_kotlin.app.WanApp
import org.lym.wanandroid_kotlin.weight.preview.loader.GlideImageLoader
import org.lym.wanandroid_kotlin.weight.preview.loader.ImageLoader
import org.lym.wanandroid_kotlin.weight.preview.style.IIndexIndicator
import org.lym.wanandroid_kotlin.weight.preview.style.IProgressIndicator
import org.lym.wanandroid_kotlin.weight.preview.style.progress.ProgressBarIndicator
import org.lym.wanandroid_kotlin.weight.preview.transfer.Transferee.OnTransfereeLongClickListener

/**
 * Transferee Attributes
 *
 *
 * Created by hitomi on 2017/1/19.
 *
 *
 * email: 196425254@qq.com
 */
class TransferConfig() {
    var nowThumbnailIndex = 0
    var offscreenPageLimit = 0
    var missPlaceHolder = 0
    var errorPlaceHolder = 0
    var backgroundColor = 0
        get() = if (field == 0) Color.BLACK else field
    var duration: Long = 0
    var isJustLoadHitImage = false
    var isEnableDragClose = false
        private set
    lateinit var missDrawable: Drawable
    lateinit var errorDrawable: Drawable
    lateinit var originImageList: MutableList<ImageView?>
    lateinit var sourceImageList: List<String>
    lateinit var thumbnailImageList: List<String>
    lateinit var progressIndicator: IProgressIndicator
    var indexIndicator: IIndexIndicator? = null
    lateinit var imageLoader: ImageLoader
    @IdRes
    var imageId = 0
    var imageView: ImageView? = null
    var listView: AbsListView? = null
    var recyclerView: RecyclerView? = null
    var longClickListener: OnTransfereeLongClickListener? = null

    fun enableDragClose(enableDragClose: Boolean) {
        isEnableDragClose = enableDragClose
    }

    /**
     * 原图路径集合是否为空
     *
     * @return true : 空
     */
    val isSourceEmpty: Boolean
        get() = sourceImageList.isEmpty()

    /**
     * 缩略图路径集合是否为空
     *
     * @return true : 空
     */
    val isThumbnailEmpty: Boolean
        get() = thumbnailImageList.isEmpty()

    class Builder {
        private var nowThumbnailIndex = 0
        private var offscreenPageLimit = 0
        private var missPlaceHolder = 0
        private var errorPlaceHolder = 0
        private var backgroundColor = 0
        private var duration: Long = 0
        private var justLoadHitImage = false
        private var enableDragClose = true
        private var missDrawable: Drawable? = null
        private var errorDrawable: Drawable? = null
        private var sourceImageList: MutableList<String>? = null
        private var thumbnailImageList: MutableList<String>? = null
        private var progressIndicator: IProgressIndicator? = null
        private var indexIndicator: IIndexIndicator? = null
        private var imageLoader: ImageLoader? = null
        @IdRes
        private var imageId = 0
        private var imageView: ImageView? = null
        private var listView: AbsListView? = null
        private var recyclerView: RecyclerView? = null
        private var longClickListener: OnTransfereeLongClickListener? = null
        /**
         * 当前缩略图在所有图片中的索引
         */
        fun setNowThumbnailIndex(nowThumbnailIndex: Int): Builder {
            this.nowThumbnailIndex = nowThumbnailIndex
            return this
        }

        /**
         *
         * ViewPager 中进行初始化并创建的页面 : 设置为当前页面加上当前页面两侧的页面
         *
         * 默认为1, 表示第一次加载3张(nowThumbnailIndex, nowThumbnailIndex
         * + 1, nowThumbnailIndex - 1);值为 2, 表示加载5张。依次类推
         *
         * 这个参数是为了优化而提供的，值越大，初始化创建的页面越多，保留的页面也
         * 越多，推荐使用默认值
         */
        fun setOffscreenPageLimit(offscreenPageLimit: Int): Builder {
            this.offscreenPageLimit = offscreenPageLimit
            return this
        }

        /**
         * 缺省的占位图(资源ID)
         */
        fun setMissPlaceHolder(missPlaceHolder: Int): Builder {
            this.missPlaceHolder = missPlaceHolder
            return this
        }

        /**
         * 图片加载错误显示的图片(资源ID)
         */
        fun setErrorPlaceHolder(errorPlaceHolder: Int): Builder {
            this.errorPlaceHolder = errorPlaceHolder
            return this
        }

        /**
         * 为 transferee 组件设置背景颜色
         */
        fun setBackgroundColor(backgroundColor: Int): Builder {
            this.backgroundColor = backgroundColor
            return this
        }

        /**
         * 动画播放时长
         */
        fun setDuration(duration: Long): Builder {
            this.duration = duration
            return this
        }

        /**
         * 仅仅只加载当前显示的图片
         */
        fun setJustLoadHitImage(justLoadHitImage: Boolean): Builder {
            this.justLoadHitImage = justLoadHitImage
            return this
        }

        /**
         * 是否可以拖拽关闭
         */
        fun enableDragClose(enableDragClose: Boolean): Builder {
            this.enableDragClose = enableDragClose
            return this
        }

        /**
         * 缺省的占位图(Drawable 格式)
         */
        fun setMissDrawable(missDrawable: Drawable?): Builder {
            this.missDrawable = missDrawable
            return this
        }

        /**
         * 图片加载错误显示的图片(Drawable 格式)
         */
        fun setErrorDrawable(errorDrawable: Drawable?): Builder {
            this.errorDrawable = errorDrawable
            return this
        }

        /**
         * 高清图的地址集合
         */
        fun setSourceImageList(sourceImageList: MutableList<String>?): Builder {
            this.sourceImageList = sourceImageList
            return this
        }

        /**
         * 缩略图地址集合
         */
        fun setThumbnailImageList(thumbnailImageList: MutableList<String>?): Builder {
            this.thumbnailImageList = thumbnailImageList;
            return this;
        }

        /**
         * 加载高清图的进度条 (默认内置 ProgressPieIndicator), 可自实现
         * IProgressIndicator 接口定义自己的图片加载进度条
         */
        fun setProgressIndicator(progressIndicator: IProgressIndicator?): Builder {
            this.progressIndicator = progressIndicator
            return this
        }

        /**
         * 图片索引指示器 (默认内置 IndexCircleIndicator), 可自实现
         * IIndexIndicator 接口定义自己的图片索引指示器
         */
        fun setIndexIndicator(indexIndicator: IIndexIndicator?): Builder {
            this.indexIndicator = indexIndicator
            return this
        }

        /**
         * 图片加载器 (默认内置 GlideImageLoader), 可自实现
         * ImageLoader 接口定义自己的图片加载器
         */
        fun setImageLoader(imageLoader: ImageLoader?): Builder {
            this.imageLoader = imageLoader
            return this
        }

        /**
         * 绑定 transferee 长按操作监听器
         */
        fun setOnLongClickListener(listener: OnTransfereeLongClickListener?): Builder {
            longClickListener = listener
            return this
        }

        fun bindListView(listView: AbsListView?, imageId: Int): TransferConfig {
            this.listView = listView
            this.imageId = imageId
            return create()
        }

        fun bindRecyclerView(recyclerView: RecyclerView?, imageId: Int): TransferConfig {
            this.recyclerView = recyclerView
            this.imageId = imageId
            return create()
        }

        fun bindImageView(
            imageView: ImageView,
            sourceImageList: MutableList<String>?
        ): TransferConfig {
            this.imageView = imageView
            this.sourceImageList = sourceImageList
            return create()
        }

        fun bindImageView(
            imageView: ImageView,
            thumbnailImageList: MutableList<String>?,
            sourceImageList: MutableList<String>?
        ): TransferConfig {
            this.imageView = imageView
            this.thumbnailImageList = thumbnailImageList
            this.sourceImageList = sourceImageList
            return create()
        }

        fun bindImageView(
            imageView: ImageView?,
            thumbnailUrl: String,
            sourceUrl: String
        ): TransferConfig {
            this.imageView = imageView
            thumbnailImageList = ArrayList()
            thumbnailImageList?.add(thumbnailUrl)
            sourceImageList = ArrayList()
            sourceImageList?.add(sourceUrl)
            return create()
        }

        fun bindImageView(
            imageView: ImageView?,
            sourceUrl: String
        ): TransferConfig {
            this.imageView = imageView
            sourceImageList = ArrayList()
            sourceImageList?.add(sourceUrl)
            return create()
        }

        private fun create(): TransferConfig {
            val config = TransferConfig()
            config.nowThumbnailIndex = nowThumbnailIndex
            config.offscreenPageLimit = offscreenPageLimit
            config.missPlaceHolder = missPlaceHolder
            config.errorPlaceHolder = errorPlaceHolder
            config.backgroundColor = backgroundColor
            config.duration = duration
            config.isJustLoadHitImage = justLoadHitImage
            config.enableDragClose(enableDragClose)
            config.missDrawable = missDrawable ?: ContextCompat.getDrawable(
                WanApp.getContext(),
                R.drawable.ic_empty_photo
            )!!
            config.errorDrawable = errorDrawable ?: ContextCompat.getDrawable(
                WanApp.getContext(),
                R.drawable.ic_empty_photo
            )!!
            config.sourceImageList = sourceImageList ?: mutableListOf()
            config.thumbnailImageList = thumbnailImageList ?: mutableListOf()
            config.progressIndicator = progressIndicator ?: ProgressBarIndicator()
            config.indexIndicator = indexIndicator
            config.imageLoader = imageLoader ?: GlideImageLoader(WanApp.getContext())
            config.imageId = imageId
            config.imageView = imageView
            config.listView = listView
            config.recyclerView = recyclerView
            config.longClickListener = longClickListener
            return config
        }
    }

    companion object {
        fun build(): Builder {
            return Builder()
        }
    }
}