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
 * 高清图图片已经加载过了，使用高清图作为缩略图。
 * 同时使用 [TransferImage.CATE_ANIMA_TOGETHER] 动画类型展示图片
 *
 *
 * Created by hitomi on 2017/5/4.
 *
 *
 * email: 196425254@qq.com
 */
 class LocalThumbState(transfer: TransferLayout) : TransferState(transfer) {
    override fun prepareTransfer(transImage: TransferImage, position: Int) {
        val config = transfer.transConfig
        val imageLoader = config.imageLoader
        val imgUrl = config.sourceImageList[position]
        imageLoader.showImage(
            imgUrl,
            transImage,
            config.missDrawable,
            null
        )
    }

    override fun createTransferIn(position: Int): TransferImage {
        val config = transfer.transConfig
        val transImage = createTransferImage(
            config.originImageList[position]
        )
        transformThumbnail(config.sourceImageList[position], transImage, true)
        transfer.addView(transImage, 1)
        return transImage
    }

    override fun transferLoad(position: Int) {
        val config = transfer.transConfig
        val imgUrl = config.sourceImageList[position]
        val targetImage = transfer.transAdapter.getImageItem(position)
        if (config.isJustLoadHitImage) { // 如果用户设置了 JustLoadHitImage 属性，说明在 prepareTransfer 中已经
// 对 TransferImage 裁剪且设置了占位图， 所以这里直接加载原图即可
            loadSourceImage(imgUrl, targetImage, targetImage?.drawable, position)
        } else {
            config.imageLoader.loadImageAsync(imgUrl, object : ThumbnailCallback {
                override fun onFinish(bitmap: Bitmap?) {
                    val placeholder: Drawable? = if (bitmap == null) config.missDrawable else BitmapDrawable(
                        transfer.context.resources,
                        bitmap
                    )
                    loadSourceImage(imgUrl, targetImage, placeholder, position)
                }
            })
        }
    }

    private fun loadSourceImage(
        imgUrl: String,
        targetImage: TransferImage?,
        drawable: Drawable?,
        position: Int
    ) {
        val config = transfer.transConfig
        config.imageLoader.showImage(imgUrl, targetImage, drawable, object : SourceCallback {
            override fun onStart() {}
            override fun onProgress(progress: Int) {}
            override fun onDelivered(status: Int, source: File?) {
                when (status) {
                    ImageLoader.STATUS_DISPLAY_SUCCESS -> {
                        if (TransferImage.STATE_TRANS_CLIP == targetImage?.getState()) targetImage.transformIn(
                            TransferImage.STAGE_SCALE
                        )
                        startPreview(targetImage, source, imgUrl, config, position)
                    }
                    ImageLoader.STATUS_DISPLAY_CANCEL -> if (targetImage?.drawable != null) {
                        startPreview(targetImage, source, imgUrl, config, position)
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
            transformThumbnail(config.sourceImageList[position], transImage, false)
            transfer.addView(transImage, 1)
        }
        return transImage
    }
}