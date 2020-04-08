package org.lym.wanandroid_kotlin.data.repository

import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.lym.wanandroid_kotlin.data.RequestObserver
import org.lym.wanandroid_kotlin.data.db.KeyWordDao
import org.lym.wanandroid_kotlin.data.model.GankMeiZhi
import org.lym.wanandroid_kotlin.data.model.MeiZhi
import org.lym.wanandroid_kotlin.http.exception.ApiException
import org.lym.wanandroid_kotlin.http.exception.ExceptionHandle
import org.lym.wanandroid_kotlin.http.getGankApiService

/**
 * Gank.io仓库
 *
 * author: liyaoming
 * email: liyaoming@bixin.cn
 * date: 2020-02-18-12:19
 */
class GankRepository : Repository {
    /**
     * 收藏文章
     *
     * @param id    文章id
     * @param requestObserver   请求回调
     * @return  返回Dispose
     */
    fun getMeiZhi(page: Int, requestObserver: RequestObserver<GankMeiZhi>) =
        getGankApiService().getGankMeiZhi(page)
            .subscribeOn(Schedulers.io()).observeOn(
                AndroidSchedulers.mainThread()
            ).subscribe({ bean ->
                if (bean.error != true) {
                    requestObserver.onSuccess(bean)
                }
            }, { e ->
                if (e is ApiException) {
                    requestObserver.onFailed(e.code, e.msg)
                } else {
                    val handle = ExceptionHandle()
                    handle.handle(e)
                    requestObserver.onFailed(handle.code, handle.msg)
                }
            })


    companion object {

        private var instance: GankRepository? = null

        fun getInstance() =
            instance ?: synchronized(this) {
                GankRepository().also {
                    instance = it
                }
            }
    }

}