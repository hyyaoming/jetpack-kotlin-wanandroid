package org.lym.wanandroid_kotlin.weight.preview.transfer

import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import org.lym.wanandroid_kotlin.weight.preview.loader.ImageLoader
import org.lym.wanandroid_kotlin.weight.preview.loader.ImageLoader.SourceCallback
import org.lym.wanandroid_kotlin.weight.preview.loader.ImageLoader.ThumbnailCallback
import org.lym.wanandroid_kotlin.weight.preview.view.image.TransferImage
import java.io.File

/**
 * 用户指定了缩略图路径，使用该路径加载缩略图，
 */
@Deprecated("")
internal class RemoteThumbState(transfer: TransferLayout) : TransferState(transfer) {
    override fun prepareTransfer(transImage: TransferImage, position: Int) {
        val config = transfer.transConfig
        val imageLoader = config.imageLoader
        val imgUrl = config.thumbnailImageList[position]
        if (imageLoader.getCache(imgUrl) != null) {
            imageLoader.showImage(
                imgUrl, transImage,
                config.missDrawable, null
            )
        } else {
            transImage.setImageDrawable(config.missDrawable)
        }
    }

    override fun createTransferIn(position: Int): TransferImage {
        val config = transfer.transConfig
        val transImage = createTransferImage(
            config.originImageList[position]
        )
        transformThumbnail(config.thumbnailImageList[position], transImage, true)
        transfer.addView(transImage, 1)
        return transImage
    }

    override fun transferLoad(position: Int) {
        val config = transfer.transConfig
        val targetImage = transfer.transAdapter.getImageItem(position)
        val imageLoader = config.imageLoader
        if (config.isJustLoadHitImage) { // 如果用户设置了 JustLoadHitImage 属性，说明在 prepareTransfer 中已经
// 对 TransferImage 裁剪且设置了占位图， 所以这里直接加载原图即可
            loadSourceImage(targetImage?.drawable, position, targetImage)
        } else {
            val thumbUrl = config.thumbnailImageList[position]
            if (imageLoader.getCache(thumbUrl) != null) {
                imageLoader.loadImageAsync(thumbUrl, object : ThumbnailCallback {
                    override fun onFinish(bitmap: Bitmap?) {
                        val placeholder: Drawable? = if (bitmap == null) {
                            config.missDrawable
                        } else {
                            BitmapDrawable(transfer.context.resources, bitmap)
                        }
                        loadSourceImage(placeholder, position, targetImage!!)
                    }
                })
            } else {
                loadSourceImage(
                    config.missDrawable,
                    position, targetImage!!
                )
            }
        }
    }

    private fun loadSourceImage(
        drawable: Drawable?,
        position: Int,
        targetImage: TransferImage?
    ) {
        val config = transfer.transConfig
        val imageLoader = config.imageLoader
        val sourceUrl = config.sourceImageList[position]
        val progressIndicator = config.progressIndicator
        progressIndicator.attach(position, transfer.transAdapter.getParentItem(position))
        imageLoader.showImage(sourceUrl, targetImage, drawable, object : SourceCallback {
            override fun onStart() {
                progressIndicator.onStart(position)
            }

            override fun onProgress(progress: Int) {
                progressIndicator.onProgress(position, progress)
            }

            override fun onDelivered(status: Int, source: File?) {
                progressIndicator.onFinish(position) // onFinish 只是说明下载完毕，并没更新图像
                when (status) {
                    ImageLoader.STATUS_DISPLAY_SUCCESS -> {
                        // 启用 TransferImage 的手势缩放功能
                        targetImage?.enable()
                        // 绑定点击关闭 Transferee
                        transfer.bindOnOperationListener(targetImage, sourceUrl, position)
                    }
                    ImageLoader.STATUS_DISPLAY_FAILED -> targetImage?.setImageDrawable(
                        config.errorDrawable
                    )
                }
            }
        })
    }

    override fun transferOut(position: Int): TransferImage? {
        var transImage: TransferImage? = null
        val config = transfer.transConfig
        val originImageList =
            config.originImageList
        if (position <= originImageList.size - 1 && originImageList[position] != null) {
            transImage = createTransferImage(originImageList[position])
            transformThumbnail(config.thumbnailImageList[position], transImage, false)
            transfer.addView(transImage, 1)
        }
        return transImage
    }
}