package org.lym.wanandroid_kotlin.http.glide

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.widget.ImageView
import androidx.annotation.DrawableRes
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestBuilder
import com.bumptech.glide.RequestManager
import com.bumptech.glide.load.DecodeFormat
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation
import com.bumptech.glide.request.RequestOptions
import org.lym.wanandroid_kotlin.app.WanApp


/**
 * Glide辅助类
 *
 * author: liyaoming
 * date: 2020-01-02-17:55
 */
class GlideHelper private constructor(context: Context) {

    private val mManager: RequestManager
    private var mBuilder: RequestBuilder<Bitmap>? = null
    private var mCache = true
    private var mPlaceHolder = 0
    private var mErrorHolder = 0
    private var mImageUrl: String? = null
    private var mBitmapTransformation: BitmapTransformation? = null

    private val options: RequestOptions
        @SuppressLint("CheckResult")
        get() {
            val options = RequestOptions()
            if (mPlaceHolder > 0) {
                options.placeholder(mPlaceHolder)
            }
            if (mErrorHolder > 0) {
                options.error(mErrorHolder)
            }
            if (mCache) {
                options.diskCacheStrategy(DiskCacheStrategy.ALL)
            } else {
                options.skipMemoryCache(true)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
            }
            if (mBitmapTransformation != null) {
                options.transform(mBitmapTransformation!!)
            }
            return options
        }

    private val builder: RequestBuilder<Bitmap>
        get() {
            if (mBuilder == null) {
                mBuilder = mManager.asBitmap()
            }
            return mBuilder!!
        }

    init {
        mManager = Glide.with(checkContext(context))
    }

    fun pauseRequests() {
        mManager.pauseRequests()
    }

    fun resumeRequests() {
        mManager.resumeRequests()
    }

    fun highQuality(): GlideHelper {
        mManager.setDefaultRequestOptions(RequestOptions().format(DecodeFormat.PREFER_ARGB_8888))
        return this
    }

    fun cache(cache: Boolean): GlideHelper {
        this.mCache = cache
        return this
    }

    fun placeHolder(@DrawableRes placeHolder: Int): GlideHelper {
        this.mPlaceHolder = placeHolder
        return this
    }

    fun errorHolder(@DrawableRes errorHolder: Int): GlideHelper {
        this.mErrorHolder = errorHolder
        return this
    }

    fun transformation(transformation: BitmapTransformation): GlideHelper {
        this.mBitmapTransformation = transformation
        return this
    }

    fun load(url: String): GlideHelper {
        this.mImageUrl = url
        mBuilder = builder.load(url)
        return this
    }

    fun load(uri: Uri): GlideHelper {
        mBuilder = builder.load(uri)
        return this
    }

    fun load(resId: Int): GlideHelper {
        mBuilder = builder.load(resId)
        return this
    }

    fun load(bitmap: Bitmap): GlideHelper {
        mBuilder = builder.load(bitmap)
        return this
    }

    fun into(imageView: ImageView) {
        builder.apply(options).into(imageView)
    }

    fun preload() {
        builder.apply(options).preload()
    }

    private fun checkContext(context: Context?): Context {
        return context ?: WanApp.getContext()
    }

    companion object {

        fun with(context: Context): GlideHelper {
            return GlideHelper(context)
        }
    }
}
