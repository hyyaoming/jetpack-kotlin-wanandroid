package org.lym.wanandroid_kotlin.mvvm.viewmodel

import androidx.lifecycle.MutableLiveData
import org.lym.wanandroid_kotlin.data.RequestObserver
import org.lym.wanandroid_kotlin.data.model.ArticleModel
import org.lym.wanandroid_kotlin.data.model.HotWordModel
import org.lym.wanandroid_kotlin.data.repository.SearchRepository

/**
 * 搜索历史ViewModel
 *
 * author: liyaoming
 * email: liyaoming@bixin.cn
 * date: 2020-01-04-18:00
 */
class HistoryViewModel(val searchRepository: SearchRepository) : AutoDisposeViewModel() {
    val hotWords = MutableLiveData<List<HotWordModel>>()
    val topArticle = MutableLiveData<List<ArticleModel>>()

    init {
        addDispose(searchRepository.getHotWork(object :
            RequestObserver<MutableList<HotWordModel>>() {
            override fun onSuccess(data: MutableList<HotWordModel>?) {
                super.onSuccess(data)
                hotWords.value = data
            }
        }))

        addDispose(searchRepository.getTopArticleList(object :
            RequestObserver<List<ArticleModel>>() {
            override fun onSuccess(data: List<ArticleModel>?) {
                super.onSuccess(data)
                topArticle.value = data
            }
        }))
    }
}