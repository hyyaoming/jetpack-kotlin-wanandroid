package org.lym.wanandroid_kotlin.data.repository

import android.util.Log
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.functions.BiFunction
import io.reactivex.schedulers.Schedulers
import org.lym.wanandroid_kotlin.data.RequestObserver
import org.lym.wanandroid_kotlin.data.model.ArticleListModel
import org.lym.wanandroid_kotlin.data.model.BannerModel
import org.lym.wanandroid_kotlin.data.model.MultipleBannerModel
import org.lym.wanandroid_kotlin.data.model.MultipleEntity
import org.lym.wanandroid_kotlin.http.Api
import org.lym.wanandroid_kotlin.http.BaseResponse
import org.lym.wanandroid_kotlin.http.Request
import org.lym.wanandroid_kotlin.http.getApiService
import org.lym.wanandroid_kotlin.utils.toast

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
    private fun bannerObservable() =
        getApiService(Api::class.java).getBanner().subscribeOn(Schedulers.io())

    /**
     * 请求首页文章列表
     *
     * @param page  页码
     */
    private fun articleObservable(page: Int) =
        getApiService(Api::class.java).getArticleList(page)

    /**
     * 获取首页文章以及banner数据
     *
     * @param requestObserver   请求回调
     * @return  返回数据
     */
    fun getBannerAndArticle(requestObserver: RequestObserver<MutableList<MultipleEntity>>): Disposable {

        val banner = bannerObservable()
        val articleList =
            articleObservable(0).subscribeOn(Schedulers.io())

        return Observable.zip(
            banner,
            articleList,
            BiFunction<BaseResponse<MutableList<BannerModel>>,
                    BaseResponse<ArticleListModel>,
                    MutableList<MultipleEntity>> { list: BaseResponse<MutableList<BannerModel>>, articleListModel: BaseResponse<ArticleListModel> ->
                val result = mutableListOf<MultipleEntity>()
                result.add(MultipleBannerModel(list.data))
                articleListModel.data?.datas?.let { result.addAll(it) }
                result
            }).subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                requestObserver.onSuccess(it)
            }, {
                it.message?.let { msg ->
                    toast(msg)
                }
            })
    }

    /**
     * 获取首页文章列表
     *
     * @param page  页码，从0开始
     */
    fun getArticleListObservable(page: Int, requestObserver: RequestObserver<ArticleListModel>) {
        Request.create(articleObservable(page)).request(requestObserver)
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