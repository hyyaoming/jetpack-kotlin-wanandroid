package org.lym.wanandroid_kotlin.weight.preview.loader

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import android.widget.ImageView
import com.blankj.utilcode.util.FileUtils
import com.blankj.utilcode.util.FileUtils.getFileName
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import org.lym.wanandroid_kotlin.weight.preview.loader.ImageLoader.Companion.STATUS_DISPLAY_FAILED
import org.lym.wanandroid_kotlin.weight.preview.loader.ImageLoader.Companion.STATUS_DISPLAY_SUCCESS
import java.io.File
import java.util.concurrent.Executors

/**
 * preview glide imageLoader
 *
 * author: ym.li
 * since: 2020/3/1
 */
class GlideImageLoader(val context: Context) : ImageLoader {
    private var callbackMap = mutableMapOf<String?, ImageLoader.SourceCallback?>()
    private val threadPool = Executors.newCachedThreadPool()

    override fun showImage(
        imageUrl: String?,
        imageView: ImageView?,
        placeholder: Drawable?,
        sourceCallback: ImageLoader.SourceCallback?
    ) {
        callbackMap[imageUrl] = sourceCallback
        sourceCallback?.onStart()
        Glide.with(imageView!!).download(imageUrl).placeholder(placeholder)
            .listener(object : RequestListener<File> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any,
                    target: Target<File>,
                    isFirstResource: Boolean
                ): Boolean {
                    callbackMap[imageUrl]?.onDelivered(STATUS_DISPLAY_FAILED, null)
                    return false
                }

                override fun onResourceReady(
                    resource: File,
                    model: Any,
                    target: Target<File>,
                    dataSource: DataSource,
                    isFirstResource: Boolean
                ): Boolean {
                    if (!imageUrl!!.endsWith(".gif")) // gif 图片需要 transferee 内部渲染，所以这里作显示
                        imageView.setImageBitmap(BitmapFactory.decodeFile(resource.absolutePath))
                    checkSaveFile(resource, getFileName(imageUrl))
                    val callback: ImageLoader.SourceCallback? = callbackMap[imageUrl]
                    if (callback != null) {
                        callback.onDelivered(STATUS_DISPLAY_SUCCESS, resource)
                        callbackMap.remove(imageUrl)
                    }
                    return false
                }
            }).preload()
    }

    private fun getCacheDir(): File {
        val cacheDir = File(context.cacheDir, CACHE_DIR)
        if (!cacheDir.exists()) cacheDir.mkdirs()
        return cacheDir
    }

    private fun checkSaveFile(file: File, fileName: String) {
        val cacheDir: File = getCacheDir()
        val exists = FileUtils.isFileExists(File(cacheDir, fileName))
        if (!exists) {
            threadPool.submit {
                val targetFile = File(cacheDir, fileName)
                FileUtils.copy(file, targetFile)
            }
        }
    }

    override fun loadImageAsync(imageUrl: String?, callback: ImageLoader.ThumbnailCallback?) {
        Glide.with(context).download(imageUrl).listener(object : RequestListener<File> {
            override fun onLoadFailed(
                e: GlideException?,
                model: Any,
                target: Target<File>,
                isFirstResource: Boolean
            ): Boolean {
                callback?.onFinish(null)
                return false
            }

            override fun onResourceReady(
                resource: File,
                model: Any,
                target: Target<File>,
                dataSource: DataSource,
                isFirstResource: Boolean
            ): Boolean {
                checkSaveFile(resource, getFileName(imageUrl))
                callback?.onFinish(BitmapFactory.decodeFile(resource.absolutePath))
                return false
            }
        }).preload()
    }

    override fun loadImageSync(imageUrl: String?): Bitmap? {
        return BitmapFactory.decodeFile(getCache(imageUrl)!!.absolutePath)
    }

    override fun getCache(url: String?): File? {
        val cacheFile = File(getCacheDir(), getFileName(url))
        return if (cacheFile.exists()) cacheFile else null
    }

    override fun clearCache() {
        threadPool.submit {
            Glide.get(context).clearMemory()
            Glide.get(context).clearDiskCache()
            FileUtils.delete(getCacheDir())
        }
    }

    companion object {
        private const val CACHE_DIR = "TransGlide"
        fun with(context: Context): GlideImageLoader {
            return GlideImageLoader(context)
        }
    }
}