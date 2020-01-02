package org.lym.wanandroid_kotlin.mvvm

import androidx.lifecycle.MutableLiveData
import org.lym.wanandroid_kotlin.data.RequestObserver
import org.lym.wanandroid_kotlin.data.model.MultipleEntity
import org.lym.wanandroid_kotlin.data.repository.IndexRepository

/**
 * 首页ViewModel
 *
 * author: liyaoming
 * email: liyaoming@bixin.cn
 * date: 2019-12-31-19:13
 */
class IndexViewModel(indexRepository: IndexRepository) : AutoDisposeViewModel() {
    val mutableData = MutableLiveData<MutableList<MultipleEntity>>()
    private var page = 0

    init {

        addDispose(indexRepository.getBannerAndArticle(object :
            RequestObserver<MutableList<MultipleEntity>>() {
            override fun onSuccess(data: MutableList<MultipleEntity>) {
                super.onSuccess(data)
                mutableData.value = data
            }
        }))

    }

}