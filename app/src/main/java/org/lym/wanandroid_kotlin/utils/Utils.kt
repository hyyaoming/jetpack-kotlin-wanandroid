package org.lym.wanandroid_kotlin.utils

import android.content.Context
import android.net.ConnectivityManager
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
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

fun RecyclerView.scrollTop(targetPosition: Int) {
    val layoutManager = layoutManager
    var firstVisibleItemPosition = 0
    if (layoutManager is GridLayoutManager) {
        val manager = getLayoutManager() as GridLayoutManager
        firstVisibleItemPosition = manager.findFirstVisibleItemPosition()
    } else if (layoutManager is LinearLayoutManager) {
        val manager = getLayoutManager() as LinearLayoutManager
        firstVisibleItemPosition = manager.findFirstVisibleItemPosition()
    }
    if (firstVisibleItemPosition > targetPosition) {
        scrollToPosition(targetPosition)
    }
    smoothScrollToPosition(0)
}


fun dip2px(dipValue: Float): Int {
    val scale = WanApp.getContext().resources.displayMetrics.density
    return (dipValue * scale + 0.5F).toInt()
}
