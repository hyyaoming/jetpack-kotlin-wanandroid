package org.lym.wanandroid_kotlin.http

/**
 * 数据基类
 *
 * author: liyaoming
 * email: liyaoming@bixin.cn
 * date: 2019-12-31-13:42
 */
class BaseResponse<T> {
    var errorCode: Int = 0
    var errorMsg: String = ""
    var data: T? = null

    fun isSuccess() = errorCode == 0
}