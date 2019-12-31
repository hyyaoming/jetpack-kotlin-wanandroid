package org.lym.wanandroid_kotlin.mvvm

import androidx.lifecycle.MutableLiveData
import org.lym.wanandroid_kotlin.data.model.BannerModel
import org.lym.wanandroid_kotlin.data.repository.IndexRepository

/**
 * author: liyaoming
 * email: liyaoming@bixin.cn
 * date: 2019-12-31-19:13
 */
class IndexViewModel(indexRepository: IndexRepository) : AutoDisposeViewModel() {
    val banners = MutableLiveData<List<BannerModel>>()

    init {
    }

}