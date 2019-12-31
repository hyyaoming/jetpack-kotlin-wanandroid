package org.lym.wanandroid_kotlin.data.repository

import org.lym.wanandroid_kotlin.data.RequestObserver
import org.lym.wanandroid_kotlin.data.model.BannerModel
import org.lym.wanandroid_kotlin.http.Api
import org.lym.wanandroid_kotlin.http.Request
import org.lym.wanandroid_kotlin.http.getApiService

/**
 * author: liyaoming
 * email: liyaoming@bixin.cn
 * date: 2019-12-31-17:49
 */
class IndexRepository : Repository {

    fun getBanner(requestObserver: RequestObserver<List<BannerModel>>) {
        Request.create(getApiService(Api::class.java).getBanner()).request(requestObserver)
    }

    companion object {

        @Volatile
        private var instance: IndexRepository? = null

        fun getInstance() =
            instance ?: synchronized(this) {
                instance ?: IndexRepository().also { instance = it }
            }
    }


}