package org.lym.wanandroid_kotlin.mvvm.viewmodel

import androidx.lifecycle.MutableLiveData
import org.lym.wanandroid_kotlin.data.RequestObserver
import org.lym.wanandroid_kotlin.data.model.MeiZhi
import org.lym.wanandroid_kotlin.data.repository.GankRepository
import org.lym.wanandroid_kotlin.utils.toast

/**
 * Gank.io ViewModel
 *
 * author: liyaoming
 * email: liyaoming@bixin.cn
 * date: 2020-02-18-17:38
 */
class GankViewModel(val repository: GankRepository) : AutoDisposeViewModel() {
    var meiZhi = MutableLiveData<MutableList<MeiZhi>>()
    var moreMeiZhi = MutableLiveData<MutableList<MeiZhi>>()
    private var page = 3;

    init {
        loadGankMeiZhi(true)
    }

    fun loadGankMeiZhi(first: Boolean) {
        if (first) {
            page == 3
        } else {
            if (page++ == 6) {
                loadEnd.value = true
                return
            }
        }
        repository.getMeiZhi(page,
            object : RequestObserver<MutableList<MeiZhi>>() {
                override fun onSuccess(data: MutableList<MeiZhi>?) {
                    super.onSuccess(data)
                    if (first) {
                        meiZhi.value = data
                    } else {
                        moreMeiZhi.value = data
                        loadMoreComplete.value = true
                    }
                }

                override fun onFailed(code: Int, msg: String) {
                    super.onFailed(code, msg)
                    toast(msg)
                }
            })
    }

}