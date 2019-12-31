package org.lym.wanandroid_kotlin.http

import org.lym.wanandroid_kotlin.http.exception.ExceptionHandle

/**
 * author: liyaoming
 * email: liyaoming@bixin.cn
 * date: 2019-12-31-16:09
 */
interface RequestCallback<T> {
    fun onStart()
    fun onSuccess(code: Int, data: T?)
    fun onFailed(code: Int, msg: String)
    fun onError(handle: ExceptionHandle)
    fun onFinish()
}