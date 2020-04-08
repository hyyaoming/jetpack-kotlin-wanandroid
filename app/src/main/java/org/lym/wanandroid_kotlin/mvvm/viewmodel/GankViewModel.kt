package org.lym.wanandroid_kotlin.mvvm.viewmodel

import androidx.lifecycle.MutableLiveData
import org.lym.wanandroid_kotlin.data.RequestObserver
import org.lym.wanandroid_kotlin.data.model.GankMeiZhi
import org.lym.wanandroid_kotlin.data.model.MeiZhi
import org.lym.wanandroid_kotlin.data.repository.GankRepository
import org.lym.wanandroid_kotlin.utils.toast

/**
 * Gank.io ViewModel
 *
 * author: liyaoming
 * date: 2020-02-18-17:38
 */
class GankViewModel(val repository: GankRepository) : AutoDisposeViewModel() {
    var meiZhi = MutableLiveData<MutableList<MeiZhi>>()
    var moreMeiZhi = MutableLiveData<MutableList<MeiZhi>>()
    private var page = 1;

    init {
        loadGankMeiZhi(true)
    }

    fun loadGankMeiZhi(first: Boolean) {
        repository.getMeiZhi(page++,
            object : RequestObserver<GankMeiZhi>() {
                override fun onSuccess(data: GankMeiZhi?) {
                    super.onSuccess(data)
                    if (first) {
                        meiZhi.value = data?.data
                    } else {
                        moreMeiZhi.value = data?.data
                        loadMoreComplete.value = true
                    }
                    if (page - 1 == data?.page_count) {
                        loadEnd.value = true
                    }
                }

                override fun onFailed(code: Int, msg: String) {
                    super.onFailed(code, msg)
                    toast(msg)
                }
            })
    }

}