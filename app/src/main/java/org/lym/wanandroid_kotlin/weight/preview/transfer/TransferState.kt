package org.lym.wanandroid_kotlin.weight.preview.transfer

import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.ImageView.ScaleType
import org.lym.wanandroid_kotlin.weight.preview.loader.ImageLoader
import org.lym.wanandroid_kotlin.weight.preview.view.image.TransferImage
import pl.droidsonroids.gif.GifDrawable
import java.io.File
import java.io.IOException

/**
 * 由于用户配置的参数不同 (例如 使用不同的 ImageLoader  / 是否指定了 thumbnailImageList 参数值) <br></br>
 * 使得 Transferee 所表现的行为不同，所以采用一组策略算法来实现以下不同的功能：
 *
 *  * 1. 图片进入 Transferee 的过渡动画
 *  * 2. 图片加载时不同的表现形式
 *  * 3. 图片从 Transferee 中出去的过渡动画
 */
abstract class TransferState(protected var transfer: TransferLayout) {
    /**
     * 由于 4.4 以下版本状态栏不可修改，所以兼容 4.4 以下版本的全屏模式时，要去除状态栏的高度
     *
     * @param oldY  y
     * @return  int
     */
    fun getTransImageLocalY(oldY: Int): Int {
        return oldY
    }

    /**
     * 获取 View 在屏幕坐标系中的坐标
     *
     * @param view 需要定位位置的 View
     * @return 坐标系数组
     */
    private fun getViewLocation(view: View): IntArray {
        val location = IntArray(2)
        view.getLocationInWindow(location)
        return location
    }

    /**
     * 依据 originImage 在屏幕中的坐标和宽高信息创建一个 TransferImage
     *
     * @param originImage 缩略图 ImageView
     * @return TransferImage
     */
    fun createTransferImage(originImage: ImageView?): TransferImage {
        val config = transfer.transConfig
        val location = getViewLocation(originImage!!)
        val transImage = TransferImage(transfer.context)
        transImage.scaleType = ScaleType.FIT_CENTER
        transImage.setOriginalInfo(
            location[0], getTransImageLocalY(location[1]),
            originImage.width, originImage.height
        )
        transImage.duration = config.duration
        transImage.layoutParams = FrameLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        transImage.setOnTransferListener(transfer.transListener)
        return transImage
    }

    /**
     * 加载 imageUrl 所关联的图片到 TransferImage 并启动 TransferImage 中的过渡动画
     *
     * @param imageUrl   当前缩略图路径
     * @param transImage [.createTransferImage] 方法创建的 TransferImage
     * @param in         true : 从缩略图到高清图动画, false : 从高清图到缩略图动画
     */
    fun transformThumbnail(
        imageUrl: String?,
        transImage: TransferImage,
        `in`: Boolean
    ) {
        val config = transfer.transConfig
        val imageLoader = config.imageLoader
        if (this is RemoteThumbState) { // RemoteThumbState
            if (imageLoader.getCache(imageUrl) != null) { // 缩略图已加载过
                loadThumbnail(imageUrl, transImage, `in`)
            } else { // 缩略图 未加载过，则使用用户配置的缺省占位图
                transImage.setImageDrawable(config.missDrawable)
                if (`in`) transImage.transformIn() else transImage.transformOut()
            }
        } else { // LocalThumbState
            loadThumbnail(imageUrl, transImage, `in`)
        }
    }

    /**
     * 图片加载完毕，开启预览
     *
     * @param targetImage 预览图片
     * @param imgUrl      图片url
     * @param config      设置
     * @param position    索引
     */
    fun startPreview(
        targetImage: TransferImage?,
        source: File?,
        imgUrl: String,
        config: TransferConfig,
        position: Int
    ) { // 启用 TransferImage 的手势缩放功能
        targetImage?.enable()
        if (imgUrl.endsWith("gif")) {
            val cache =
                source ?: config.imageLoader.getCache(imgUrl)
            if (cache != null) {
                try {
                    targetImage?.setImageDrawable(GifDrawable(cache.path))
                } catch (ignored: IOException) {
                }
            }
        }
        // 绑定点击关闭 Transferee
        transfer.bindOnOperationListener(targetImage, imgUrl, position)
    }

    /**
     * 加载 imageUrl 所关联的图片到 TransferImage 中
     *
     * @param imageUrl   图片路径
     * @param transImage    TransferImage
     * @param in         true: 表示从缩略图到 Transferee, false: 从 Transferee 到缩略图
     */
    private fun loadThumbnail(
        imageUrl: String?,
        transImage: TransferImage,
        `in`: Boolean
    ) {
        val config = transfer.transConfig
        val imageLoader = config.imageLoader
        val drawable = imageLoader.loadImageSync(imageUrl)
        if (drawable == null) transImage.setImageDrawable(config.missDrawable) else transImage.setImageBitmap(
            drawable
        )
        if (`in`) transImage.transformIn() else transImage.transformOut()
    }

    /**
     * 当用户使用 justLoadHitImage 属
     * 性时，需要使用 prepareTransfer 方法提前让 ViewPager 对应
     * position 处的 TransferImage 剪裁并设置占位图
     *
     * @param transImage ViewPager 中 position 位置处的 TransferImage
     * @param position   当前点击的图片索引
     */
    abstract fun prepareTransfer(transImage: TransferImage, position: Int)

    /**
     * 创建一个 TransferImage 放置在 Transferee 中指定位置，并播放从缩略图到 Transferee 的过渡动画
     *
     * @param position 进入到 Transferee 之前，用户在图片列表中点击的图片的索引
     * @return 创建的 TransferImage
     */
    abstract fun createTransferIn(position: Int): TransferImage

    /**
     * 从网络或者从 [ImageLoader] 指定的缓存中加载 SourceImageList.get(position) 对应的图片
     *
     * @param position 原图片路径索引
     */
    abstract fun transferLoad(position: Int)

    /**
     * 创建一个 TransferImage 放置在 Transferee 中指定位置，并播放从 Transferee 到 缩略图的过渡动画
     *
     * @param position 当前点击的图片索引
     * @return 创建的 TransferImage
     */
    abstract fun transferOut(position: Int): TransferImage?

}