package org.lym.wanandroid_kotlin.http.exception

import com.google.gson.JsonParseException
import org.json.JSONException
import org.lym.wanandroid_kotlin.isConnected
import retrofit2.HttpException
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import java.text.ParseException
import javax.net.ssl.SSLException

/**
 * author: liyaoming
 * email: liyaoming@bixin.cn
 * date: 2019-12-31-15:44
 */
class ExceptionHandle {

    private var e: Throwable? = null
    private var code: Int = 0
    private var msg: String = ""

    fun handle(e: Throwable) {
        this.e = e
        this.code = onGetCode(e)
        this.msg = onGetMsg(code)
    }

    /**
     * 重写该方法去返回异常对应的错误码
     *
     * @param e Throwable
     * @return 错误码
     */
    private fun onGetCode(e: Throwable): Int {
        return if (isConnected()) {
            Code.NET
        } else {
            if (e is SocketTimeoutException) {
                Code.TIMEOUT
            } else if (e is HttpException) {
                Code.HTTP
            } else if (e is UnknownHostException || e is ConnectException) {
                Code.HOST
            } else if (e is JsonParseException || e is ParseException || e is JSONException) {
                Code.JSON
            } else if (e is SSLException) {
                Code.SSL
            } else {
                Code.UNKNOWN
            }
        }
    }

    /**
     * 重写该方法去返回错误码对应的错误信息
     *
     * @param code 错误码
     * @return 错误信息
     */
    private fun onGetMsg(code: Int): String {
        return when (code) {
            Code.NET -> "网络连接失败，请检查网络设置"
            Code.TIMEOUT -> "网络状况不稳定，请稍后重试"
            Code.JSON -> "JSON解析异常"
            Code.HTTP -> "请求错误，请稍后重试"
            Code.HOST -> "服务器连接失败，请检查网络设置"
            Code.SSL -> "证书验证失败"
            else -> "未知错误，请稍后重试"
        }
    }

    interface Code {
        companion object {
            const val UNKNOWN = -1
            const val NET = 0
            const val TIMEOUT = 1
            const val JSON = 2
            const val HTTP = 3
            const val HOST = 4
            const val SSL = 5
        }
    }
}