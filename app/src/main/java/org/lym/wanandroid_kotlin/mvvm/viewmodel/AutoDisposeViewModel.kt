package org.lym.wanandroid_kotlin.mvvm.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

/**
 * ViewModel基类
 *
 * author: liyaoming
 * date: 2019-12-31-19:07
 */
open class AutoDisposeViewModel : ViewModel() {
    /**
     * 当前recyclerView是否加载完毕
     */
    val loadEnd = MutableLiveData<Boolean>()
    /**
     * 当前加载结束
     */
    val loadMoreComplete = MutableLiveData<Boolean>()
    /**
     * 页面数据进行网络请求时的loading状态变更
     */
    val requestLoading = MutableLiveData<Int>()
    /**
     * 单点网络请求loading,比如收藏啊，取消收藏之类的
     */
    val requestTipLoading = MutableLiveData<Int>()

    private val dispose by lazy {
        CompositeDisposable()
    }

    protected fun addDispose(disposable: Disposable) {
        dispose.add(disposable)
    }

    override fun onCleared() {
        super.onCleared()
        dispose.clear()
    }
}