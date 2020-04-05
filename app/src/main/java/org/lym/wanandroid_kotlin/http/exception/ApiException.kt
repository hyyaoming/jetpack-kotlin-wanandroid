package org.lym.wanandroid_kotlin.http.exception

/**
 * author: liyaoming
 * date: 2019-12-31-15:41
 */
class ApiException(var code: Int, var msg: String) : Exception("$msg(code=$code)") {
}