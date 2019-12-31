package org.lym.wanandroid_kotlin

import android.content.Context
import android.net.ConnectivityManager
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