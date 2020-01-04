package org.lym.wanandroid_kotlin.http

import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import org.lym.wanandroid_kotlin.http.exception.ApiException
import org.lym.wanandroid_kotlin.http.exception.ExceptionHandle

/**
 * 描述：网络请求
 *
 * author: liyaoming
 * email: liyaoming@bixin.cn
 * date: 2019-12-31-09:59
 */
class HttpRequest<T, R : BaseResponse<T>> private constructor(observable: Observable<R>) {
    private val mObservable: Observable<R> =
        observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())

    /**
     * 发起请求并设置成功回调
     *
     * @return Disposable 用于中断请求，管理请求生命周期
     */
    fun request(callback: RequestCallback<T>): Disposable {
        return mObservable.subscribe({ bean ->
            if (!bean.isSuccess()) {
                throw ApiException(bean.errorCode, bean.errorMsg)
            }
            callback.onSuccess(bean.data)
        }, { e ->
            if (e is ApiException) {
                callback.onFailed(e.code, e.msg)
            } else {
                val handle = ExceptionHandle()
                handle.handle(e)
                callback.onError(handle)
            }
            callback.onFinish()
        }, {
            callback.onFinish()
        }, {
            callback.onStart()
        })
    }

    companion object {
        fun <T, R : BaseResponse<T>> create(observable: Observable<R>): HttpRequest<T, R> {
            return HttpRequest<T, R>(observable)
        }
    }
}
