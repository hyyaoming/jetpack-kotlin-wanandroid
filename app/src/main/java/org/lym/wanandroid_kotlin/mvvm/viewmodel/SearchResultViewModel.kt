package org.lym.wanandroid_kotlin.mvvm.viewmodel

import androidx.lifecycle.MutableLiveData
import org.lym.wanandroid_kotlin.data.RequestObserver
import org.lym.wanandroid_kotlin.data.model.ArticleListModel
import org.lym.wanandroid_kotlin.data.model.ArticleModel
import org.lym.wanandroid_kotlin.data.model.CommonModel
import org.lym.wanandroid_kotlin.data.repository.SearchRepository
import org.lym.wanandroid_kotlin.utils.toast

/**
 * 搜索结果
 *
 * author: liyaoming
 * date: 2020-01-06-14:52
 */
class SearchResultViewModel(private val searchRepository: SearchRepository) :
    AutoDisposeViewModel() {

    val articleCollect = MutableLiveData<Int>()
    var searchKey: String? = ""
    val searchResult = MutableLiveData<MutableList<ArticleModel>>()
    val searchResultMore = MutableLiveData<MutableList<ArticleModel>>()
    var page = 0

    /**
     * 获取搜索结果
     *
     * @param key   搜索关键字
     */
    fun searchArticle(key: String? = "", more: Boolean = false) {
        key?.let {
            searchKey = key
            searchRepository.searchArticle(page, it, object : RequestObserver<ArticleListModel>() {
                override fun onSuccess(data: ArticleListModel?) {
                    super.onSuccess(data)
                    data?.apply {
                        page = curPage
                        if (more) {
                            searchResultMore.value = datas
                        } else {
                            searchResult.value = datas
                        }
                        loadMoreComplete.value = true
                        if (over) {
                            loadEnd.value = false
                        }
                    }
                }
            })
        }
    }

    fun loadMore() {
        searchArticle(searchKey, true)
    }

    /**
     * 取消收藏文章
     *
     * @param model 文章数据
     * @param position  下标
     */
    fun unCollect(model: ArticleModel, position: Int) {
        addDispose(searchRepository.unCollect(model.id, object : RequestObserver<CommonModel>() {
            override fun onSuccess(data: CommonModel?) {
                super.onSuccess(data)
                toast("已取消收藏")
                model.collect = !model.collect
                articleCollect.value = position
            }

            override fun onFailed(code: Int, msg: String) {
                super.onFailed(code, msg)
                toast(msg)
            }
        }))
    }

    /**
     * 收藏文章
     *
     * @param model 文章数据
     * @param position  下标
     */
    fun collect(model: ArticleModel, position: Int) {
        addDispose(searchRepository.collect(model.id, object : RequestObserver<CommonModel>() {
            override fun onSuccess(data: CommonModel?) {
                super.onSuccess(data)
                toast("已收藏")
                model.collect = !model.collect
                articleCollect.value = position
            }

            override fun onFailed(code: Int, msg: String) {
                super.onFailed(code, msg)
                toast(msg)
            }
        }))
    }

}