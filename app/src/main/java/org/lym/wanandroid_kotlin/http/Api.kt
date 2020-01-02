package org.lym.wanandroid_kotlin.http

import io.reactivex.Observable
import org.lym.wanandroid_kotlin.data.model.ArticleListModel
import org.lym.wanandroid_kotlin.data.model.BannerModel
import retrofit2.http.GET
import retrofit2.http.Path

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
     * @return  返回banner数据
     */
    @GET("banner/json")
    fun getBanner(): Observable<BaseResponse<MutableList<BannerModel>>>

    /**
     *  获取首页文章列表
     *  @param page 页码从0开始
     *
     * @return  返回banner数据
     */
    @GET("article/list/{page}/json")
    fun getArticleList(@Path("page") page: Int): Observable<BaseResponse<ArticleListModel>>

}

fun <T> getApiService(clazz: Class<T>): T {
    return HttpClient.getInstance().getService(clazz)
}