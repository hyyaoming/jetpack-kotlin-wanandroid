package org.lym.wanandroid_kotlin.http

import io.reactivex.Observable
import org.lym.wanandroid_kotlin.data.model.BannerModel
import retrofit2.http.GET

/**
 * 所有接口定义类
 *
 * author: liyaoming
 * email: liyaoming@bixin.cn
 * date: 2019-12-31-13:38
 */
interface Api {

    //-------------------------玩Android相关Api-------------
    /**
     *  获取banner数据
     *
     * @return
     */
    @GET("banner/json")
    fun getBanner(): Observable<BaseResponse<List<BannerModel>>>
}

fun <T> getApiService(clazz: Class<T>): T {
    return HttpClient.getInstance().getService(clazz)
}