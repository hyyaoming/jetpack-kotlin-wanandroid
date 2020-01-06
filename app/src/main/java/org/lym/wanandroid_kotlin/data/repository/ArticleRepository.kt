package org.lym.wanandroid_kotlin.data.repository

import org.lym.wanandroid_kotlin.data.RequestObserver
import org.lym.wanandroid_kotlin.data.model.ArticleListModel
import org.lym.wanandroid_kotlin.data.model.ArticleModel
import org.lym.wanandroid_kotlin.data.model.CommonModel
import org.lym.wanandroid_kotlin.http.HttpRequest
import org.lym.wanandroid_kotlin.http.getApiService

/**
 * 文章仓库基类
 *
 * author: liyaoming
 * email: liyaoming@bixin.cn
 * date: 2020-01-06-17:38
 */
abstract class ArticleRepository : Repository {
    /**
     * 收藏文章
     *
     * @param id    文章id
     * @param requestObserver   请求回调
     * @return  返回Dispose
     */
    fun collect(id: Int, requestObserver: RequestObserver<CommonModel>) =
        HttpRequest.create(getApiService().collect(id)).request(requestObserver)

    /**
     * 取消收藏
     *
     * @param id    文章id
     * @param requestObserver   请求回调
     * @return 返回Dispose
     */
    fun unCollect(id: Int, requestObserver: RequestObserver<CommonModel>) =
        HttpRequest.create(getApiService().unCollect(id)).request(requestObserver)


    /**
     * 请求首页文章列表
     *
     * @param page  页码
     */
    fun articleObservable(page: Int) =
        getApiService().getArticleList(page)

    /**
     * 获取首页文章列表
     *
     * @param page  页码，从0开始
     */
    fun getArticleListObservable(
        page: Int,
        requestObserver: RequestObserver<ArticleListModel>
    ) = HttpRequest.create(articleObservable(page)).request(requestObserver)

    /**
     * 获取置顶文章
     * @param requestObserver   请求回调
     */
    fun getTopArticleList(requestObserver: RequestObserver<MutableList<ArticleModel>>) =
        HttpRequest.create(getApiService().getTopArticleList()).request(requestObserver)

    /**
     * 搜索文章
     *
     * @param page  页码
     * @param key  搜索关键字
     * @param requestObserver   请求回调
     */
    fun searchArticle(page: Int, key: String, requestObserver: RequestObserver<ArticleListModel>) =
        HttpRequest.create(getApiService().searchArticle(page, key)).request(requestObserver)
}