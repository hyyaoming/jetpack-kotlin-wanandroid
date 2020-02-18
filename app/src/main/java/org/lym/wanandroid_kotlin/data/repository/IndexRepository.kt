package org.lym.wanandroid_kotlin.data.repository

import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import org.lym.wanandroid_kotlin.data.model.BannerModel
import org.lym.wanandroid_kotlin.http.BaseResponse
import org.lym.wanandroid_kotlin.http.getWanApiService

/**
 * 首页请求仓库
 *
 * author: liyaoming
 * email: liyaoming@bixin.cn
 * date: 2019-12-31-17:49
 */
class IndexRepository : ArticleRepository() {

    /**
     * 请求banner数据
     */
    fun bannerObservable(): Observable<BaseResponse<MutableList<BannerModel>>> =
        getWanApiService().getBanner().subscribeOn(Schedulers.io())

    companion object {

        @Volatile
        private var instance: IndexRepository? = null

        fun getInstance() =
            instance ?: synchronized(this) {
                instance ?: IndexRepository().also { instance = it }
            }
    }


}