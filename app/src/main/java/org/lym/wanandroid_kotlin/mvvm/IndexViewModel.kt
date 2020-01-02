package org.lym.wanandroid_kotlin.mvvm

import androidx.lifecycle.MutableLiveData
import com.chad.library.adapter.base.entity.MultiItemEntity
import org.lym.wanandroid_kotlin.data.RequestObserver
import org.lym.wanandroid_kotlin.data.repository.IndexRepository

/**
 * 首页ViewModel
 *
 * author: liyaoming
 * email: liyaoming@bixin.cn
 * date: 2019-12-31-19:13
 */
class IndexViewModel(indexRepository: IndexRepository) : AutoDisposeViewModel() {
    val mutableData = MutableLiveData<MutableList<MultiItemEntity>>()
    private var page = 0

    init {
        addDispose(indexRepository.getBannerAndArticle(object :
            RequestObserver<MutableList<MultiItemEntity>>() {
            override fun onSuccess(data: MutableList<MultiItemEntity>) {
                super.onSuccess(data)
                mutableData.value = data
            }
        }))

    }

}