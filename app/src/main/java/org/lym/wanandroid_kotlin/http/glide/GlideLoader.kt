package org.lym.wanandroid_kotlin.http.glide

import android.widget.ImageView
import org.lym.wanandroid_kotlin.R


/**
 * author: liyaoming
 * date: 2020-01-02-17:55
 */
object GlideLoader {

    fun image(imageView: ImageView, url: String?) {
        url?.let {
            GlideHelper.with(imageView.context)
                .errorHolder(R.drawable.image_holder)
                .placeHolder(R.drawable.image_holder)
                .cache(true)
                .load(it)
                .into(imageView)
        }
    }
}
