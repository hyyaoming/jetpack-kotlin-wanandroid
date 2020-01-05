package org.lym.wanandroid_kotlin.data.repository

import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import org.lym.wanandroid_kotlin.data.RequestObserver
import org.lym.wanandroid_kotlin.data.model.ArticleListModel
import org.lym.wanandroid_kotlin.data.model.BannerModel
import org.lym.wanandroid_kotlin.data.model.CommonModel
import org.lym.wanandroid_kotlin.http.BaseResponse
import org.lym.wanandroid_kotlin.http.HttpRequest
import org.lym.wanandroid_kotlin.http.getApiService

/**
 * 首页请求仓库
 *
 * author: liyaoming
 * email: liyaoming@bixin.cn
 * date: 2019-12-31-17:49
 */
class IndexRepository : Repository {

    /**
     * 请求首页数据
     **/
    fun bannerObservable(): Observable<BaseResponse<MutableList<BannerModel>>> =
        getApiService().getBanner().subscribeOn(Schedulers.io())

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

    companion object {

        @Volatile
        private var instance: IndexRepository? = null

        fun getInstance() =
            instance ?: synchronized(this) {
                instance ?: IndexRepository().also { instance = it }
            }
    }


}