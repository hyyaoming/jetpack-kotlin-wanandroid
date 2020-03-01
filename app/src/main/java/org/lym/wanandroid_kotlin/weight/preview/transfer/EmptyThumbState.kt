package org.lym.wanandroid_kotlin.weight.preview.transfer

import android.graphics.drawable.Drawable
import org.lym.wanandroid_kotlin.weight.preview.loader.ImageLoader
import org.lym.wanandroid_kotlin.weight.preview.loader.ImageLoader.SourceCallback
import org.lym.wanandroid_kotlin.weight.preview.view.image.TransferImage
import java.io.File

/**
 * 高清图尚未加载，使用原 ImageView 中显示的图片作为缩略图。
 * 同时使用 [TransferImage.CATE_ANIMA_APART] 动画类型展示图片
 */
class EmptyThumbState(transfer: TransferLayout) : TransferState(transfer) {
    override fun prepareTransfer(transImage: TransferImage, position: Int) {
        transImage.setImageDrawable(clipAndGetPlachHolder(transImage, position))
    }

    override fun createTransferIn(position: Int): TransferImage {
        val originImage = transfer.transConfig
            .originImageList[position]
        val transImage = createTransferImage(originImage)
        transImage.setImageDrawable(originImage?.drawable)
        transImage.transformIn(TransferImage.STAGE_TRANSLATE)
        transfer.addView(transImage, 1)
        return transImage
    }

    override fun transferLoad(position: Int) {
        val adapter = transfer.transAdapter
        val config = transfer.transConfig
        val imgUrl = config.sourceImageList[position]
        val targetImage = adapter.getImageItem(position)
        val placeHolder: Drawable
        placeHolder =
            if (config.isJustLoadHitImage) { // 如果用户设置了 JustLoadHitImage 属性，说明在 prepareTransfer 中已经
// 对 TransferImage 裁剪过了， 所以只需要获取 Drawable 作为占位图即可
                getPlaceHolder(position)
            } else {
                clipAndGetPlachHolder(targetImage!!, position)
            }
        val progressIndicator = config.progressIndicator
        progressIndicator.attach(position, adapter.getParentItem(position))
        config.imageLoader.showImage(imgUrl, targetImage,
            placeHolder, object : SourceCallback {
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
                            targetImage?.transformIn(TransferImage.STAGE_SCALE)
                            startPreview(targetImage!!, source, imgUrl, config, position)
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

    override fun transferOut(position: Int): TransferImage {
        var transImage: TransferImage? = null
        val config = transfer.transConfig
        val originImageList =
            config.originImageList
        if (position <= originImageList.size - 1 && originImageList[position] != null) {
            transImage = createTransferImage(originImageList[position]!!)
            val thumbnailDrawable = transfer.transAdapter.getImageItem(
                config.nowThumbnailIndex
            )?.drawable
            transImage.setImageDrawable(thumbnailDrawable)
            transImage.transformOut(TransferImage.STAGE_TRANSLATE)
            transfer.addView(transImage, 1)
        }
        return transImage!!
    }

    /**
     * 获取 position 位置处的 占位图，如果 position 超出下标，获取 MissDrawable
     *
     * @param position 图片索引
     * @return 占位图
     */
    private fun getPlaceHolder(position: Int): Drawable {
        val placeHolder: Drawable
        val config = transfer.transConfig
        val originImage = config.originImageList[position]
        placeHolder = if (originImage != null) {
            originImage.drawable
        } else {
            config.missDrawable
        }
        return placeHolder
    }

    /**
     * 裁剪用于显示 PlachHolder 的 TransferImage
     *
     * @param targetImage 被裁剪的 TransferImage
     * @param position    图片索引
     * @return 被裁减的 TransferImage 中显示的 Drawable
     */
    private fun clipAndGetPlachHolder(targetImage: TransferImage, position: Int): Drawable {
        val config = transfer.transConfig
        val placeHolder = getPlaceHolder(position)
        val clipSize = IntArray(2)
        val originImage = config.originImageList[position]
        if (originImage != null) {
            clipSize[0] = originImage.width
            clipSize[1] = originImage.height
        }
        clipTargetImage(targetImage, placeHolder, clipSize)
        return placeHolder
    }

    /**
     * 裁剪 ImageView 显示图片的区域
     *
     * @param targetImage    被裁减的 ImageView
     * @param originDrawable 缩略图 Drawable
     * @param clipSize       裁剪的尺寸数组
     */
    private fun clipTargetImage(
        targetImage: TransferImage,
        originDrawable: Drawable,
        clipSize: IntArray
    ) {
        val displayMetrics =
            transfer.context.resources.displayMetrics
        val width = displayMetrics.widthPixels
        val height = getTransImageLocalY(displayMetrics.heightPixels)
        targetImage.setOriginalInfo(
            originDrawable,
            clipSize[0], clipSize[1],
            width, height
        )
        targetImage.transClip()
    }
}