package org.lym.wanandroid_kotlin.data

import androidx.lifecycle.MutableLiveData

/**
 * author: liyaoming
 * date: 2019-12-31-18:45
 */
open class RequestObserver<T>(
    var loadState: MutableLiveData<Int>? = null,
    val tipLoadState: MutableLiveData<Int>? = null
) {

    open fun onSuccess(data: T?) {
    }

    open fun onFailed(code: Int, msg: String) {
    }
}