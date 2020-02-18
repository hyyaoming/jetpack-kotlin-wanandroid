package org.lym.wanandroid_kotlin.http

import com.franmontiel.persistentcookiejar.PersistentCookieJar
import com.franmontiel.persistentcookiejar.cache.SetCookieCache
import com.franmontiel.persistentcookiejar.persistence.SharedPrefsCookiePersistor
import okhttp3.OkHttpClient
import org.lym.wanandroid_kotlin.app.WanApp
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

    private var retrofits: MutableMap<String, Retrofit> = hashMapOf()

    private val cookieJar by lazy {
        PersistentCookieJar(
            SetCookieCache(),
            SharedPrefsCookiePersistor(WanApp.getContext())
        )
    }

    private fun getRetrofit(baseUrl: String): Retrofit {
        return if (retrofits.containsKey(baseUrl)) {
            retrofits.getValue(baseUrl)
        } else {
            val client = OkHttpClient.Builder().cookieJar(cookieJar).build()
            val retrofit = Retrofit.Builder().client(client).baseUrl(baseUrl)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build()
            retrofits[baseUrl] = retrofit
            retrofit
        }
    }

    fun <T> getService(baseUrl: String, clazz: Class<T>): T {
        return getInstance().getRetrofit(baseUrl).create(clazz)
    }

    fun <T> getService(clazz: Class<T>): T {
        return getService(BASE_URL, clazz)
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