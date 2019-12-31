package org.lym.wanandroid_kotlin.mvvm

import androidx.lifecycle.ViewModel
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

/**
 * author: liyaoming
 * email: liyaoming@bixin.cn
 * date: 2019-12-31-19:07
 */
open class AutoDisposeViewModel : ViewModel() {
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