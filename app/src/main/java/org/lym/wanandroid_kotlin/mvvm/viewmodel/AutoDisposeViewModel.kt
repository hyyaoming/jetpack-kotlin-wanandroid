package org.lym.wanandroid_kotlin.mvvm.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

/**
 * author: liyaoming
 * email: liyaoming@bixin.cn
 * date: 2019-12-31-19:07
 */
open class AutoDisposeViewModel : ViewModel() {
    val loadEnd = MutableLiveData<Boolean>()
    val loadMoreComplete = MutableLiveData<Boolean>()

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