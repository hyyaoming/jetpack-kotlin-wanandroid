package org.lym.wanandroid_kotlin.http

import okhttp3.OkHttpClient
import org.lym.wanandroid_kotlin.common.BASE_URL
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

/**
 * 网络请求配置类
 *
 * author: liyaoming
 * email: liyaoming@bixin.cn
 * date: 2019-12-31-09:59
 */
class HttpClient private constructor() {

    private val mRetrofit: Retrofit by lazy {
        val client = OkHttpClient.Builder().build()
        Retrofit.Builder().client(client).baseUrl(BASE_URL)
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    fun <T> getService(clazz: Class<T>): T {
        return getInstance().mRetrofit.create(clazz)
    }

    companion object {
        @Volatile
        private var instance: HttpClient? = null

        fun getInstance(): HttpClient {
            return instance ?: synchronized(this) {
                instance ?: HttpClient().also { instance = it }
            }
        }
    }
}