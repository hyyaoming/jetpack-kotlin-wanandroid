package org.lym.wanandroid_kotlin.data

import org.lym.wanandroid_kotlin.http.RequestCallback
import org.lym.wanandroid_kotlin.http.exception.ExceptionHandle

/**
 * author: liyaoming
 * email: liyaoming@bixin.cn
 * date: 2019-12-31-18:45
 */
open class RequestObserver<T> : RequestCallback<T> {
    override fun onStart() {
    }

    override fun onError(handle: ExceptionHandle) {
    }

    override fun onFinish() {
    }

    override fun onSuccess(data: T?) {
    }

    override fun onFailed(code: Int, msg: String) {
    }
}