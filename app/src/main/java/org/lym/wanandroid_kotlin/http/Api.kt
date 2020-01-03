package org.lym.wanandroid_kotlin.http

import io.reactivex.Observable
import org.lym.wanandroid_kotlin.data.model.ArticleListModel
import org.lym.wanandroid_kotlin.data.model.BannerModel
import org.lym.wanandroid_kotlin.data.model.CommonModel
import org.lym.wanandroid_kotlin.data.model.LoginModel
import retrofit2.http.*

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

    /**
     * 登陆请求
     *
     * @param username
     * @param password
     */
    @FormUrlEncoded
    @POST("user/login")
    fun login(
        @Field("username") username: String,
        @Field("password") password: String
    ): Observable<BaseResponse<LoginModel>>

    /**
     * 收藏站内文章
     *
     * @param id    文章id
     * @return  返回信息
     */
    @POST("lg/collect/{id}/json")
    fun collect(@Path("id") id: Int): Observable<BaseResponse<CommonModel>>

    /**
     * 取消收藏
     *
     * @param id   文章id
     * @return  返回信息
     */
    @POST("lg/uncollect_originId/{id}/json")
    fun unCollect(@Path("id") id: Int): Observable<BaseResponse<CommonModel>>

}

fun getApiService(): Api {
    return HttpClient.getInstance().getService(Api::class.java)
}