package org.lym.wanandroid_kotlin.mvvm.viewmodel

import androidx.lifecycle.MutableLiveData
import org.lym.wanandroid_kotlin.data.RequestObserver
import org.lym.wanandroid_kotlin.data.db.model.KeyWord
import org.lym.wanandroid_kotlin.data.model.ArticleModel
import org.lym.wanandroid_kotlin.data.model.HotWordModel
import org.lym.wanandroid_kotlin.data.repository.SearchRepository
import org.lym.wanandroid_kotlin.utils.toast

/**
 * 搜索历史ViewModel
 *
 * author: liyaoming
 * email: liyaoming@bixin.cn
 * date: 2020-01-04-18:00
 */
class HistoryViewModel(val searchRepository: SearchRepository) : AutoDisposeViewModel() {
    val deleteWords = MutableLiveData<Boolean>()
    val hotWords = MutableLiveData<List<HotWordModel>>()
    val topArticle = MutableLiveData<MutableList<ArticleModel>>()

    init {
        //获取搜索热词
        addDispose(searchRepository.getHotWork(object :
            RequestObserver<MutableList<HotWordModel>>() {
            override fun onSuccess(data: MutableList<HotWordModel>?) {
                super.onSuccess(data)
                hotWords.value = data
            }
        }))

        //获取置顶文章
        addDispose(searchRepository.getTopArticleList(object :
            RequestObserver<MutableList<ArticleModel>>() {
            override fun onSuccess(data: MutableList<ArticleModel>?) {
                super.onSuccess(data)
                topArticle.value = data
            }
        }))
    }

    /**
     * 插入一条历史记录
     *
     * @param key
     */
    fun insertWord(key: String) {
        addDispose(searchRepository.insertSearchWord(key))
    }

    /**
     * 删除所有历史记录
     *
     * @param words 历史记录
     */
    fun clearHistory(words: List<KeyWord>) {
        addDispose(searchRepository.deleteWords(words, object : RequestObserver<Boolean>() {
            override fun onSuccess(data: Boolean?) {
                super.onSuccess(data)
                deleteWords.value = data
            }

            override fun onFailed(code: Int, msg: String) {
                super.onFailed(code, msg)
                toast("executor sql error")
            }
        }))
    }
}