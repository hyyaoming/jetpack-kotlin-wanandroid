package org.lym.wanandroid_kotlin.http

import io.reactivex.Observable
import org.lym.wanandroid_kotlin.common.GANK_IO_URL
import org.lym.wanandroid_kotlin.data.model.*
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
     *  置顶文章
     *
     * @return  返回数据
     */
    @GET("article/top/json")
    fun getTopArticleList(): Observable<BaseResponse<MutableList<ArticleModel>>>

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

    /**
     * 获取搜索热词
     *
     * @return  返回热词
     */
    @GET("hotkey/json")
    fun hotWork(): Observable<BaseResponse<MutableList<HotWordModel>>>

    /**
     *  搜索文章
     *
     * @return  返回数据
     */
    @FormUrlEncoded
    @POST("article/query/{page}/json")
    fun searchArticle(@Path("page") page: Int, @Field("k") key: String): Observable<BaseResponse<ArticleListModel>>

    //-------------------------玩Android相关Api-------------
    /**
     * 获取Gank.Io提供的妹纸图
     *
     * @param page  页码
     * @return  返回Observable观察流
     */
    @GET("api/data/福利/10/{page}")
    fun getGankMeiZhi(@Path("page") page: Int): Observable<GankMeiZhi>

}

fun getWanApiService(): Api {
    return HttpClient.getInstance().getService(Api::class.java)
}

fun getGankApiService(): Api {
    return HttpClient.getInstance().getService(GANK_IO_URL, Api::class.java)
}