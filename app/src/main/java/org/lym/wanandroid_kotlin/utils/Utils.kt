package org.lym.wanandroid_kotlin.utils

import android.content.Context
import android.net.ConnectivityManager
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import org.lym.wanandroid_kotlin.app.WanApp

/**
 * author: liyaoming
 * email: liyaoming@bixin.cn
 * date: 2019-12-31-15:45
 */
fun isConnected(): Boolean {
    val connectivityManager =
        WanApp.getContext().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val networkInfo = connectivityManager.activeNetworkInfo
    if (networkInfo != null) {
        return networkInfo.isAvailable
    }
    return false
}

fun toast(content: CharSequence?) {
    if (!content.isNullOrEmpty()) {
        Toast.makeText(WanApp.getContext(), content, Toast.LENGTH_SHORT).apply {
            setGravity(Gravity.CENTER, 0, 0)
        }.show()
    }
}

fun Context.view(resource: Int, container: ViewGroup?, attchToRoot: Boolean): View {
    return LayoutInflater.from(this).inflate(resource, container, attchToRoot)
}


fun dip2px(dipValue: Float): Int {
    val scale = WanApp.getContext().resources.displayMetrics.density
    return (dipValue * scale + 0.5F).toInt()
}
