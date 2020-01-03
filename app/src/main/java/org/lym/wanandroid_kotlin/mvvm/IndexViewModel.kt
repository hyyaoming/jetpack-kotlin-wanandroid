package org.lym.wanandroid_kotlin.mvvm

import androidx.lifecycle.MutableLiveData
import com.chad.library.adapter.base.entity.MultiItemEntity
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.BiFunction
import io.reactivex.schedulers.Schedulers
import org.lym.wanandroid_kotlin.data.RequestObserver
import org.lym.wanandroid_kotlin.data.model.*
import org.lym.wanandroid_kotlin.data.repository.IndexRepository
import org.lym.wanandroid_kotlin.http.BaseResponse
import org.lym.wanandroid_kotlin.utils.toast

/**
 * 首页ViewModel
 *
 * author: liyaoming
 * email: liyaoming@bixin.cn
 * date: 2019-12-31-19:13
 */
class IndexViewModel(var indexRepository: IndexRepository) : AutoDisposeViewModel() {
    val mutableData = MutableLiveData<MutableList<MultiItemEntity>>()
    val articleData = MutableLiveData<MutableList<ArticleModel>>()
    val articleCollect = MutableLiveData<Int>()
    val loadEnd = MutableLiveData<Boolean>()
    private var page = 0

    init {
        page = 0

        val banner = indexRepository.bannerObservable()
        val articleList = indexRepository.articleObservable(0).subscribeOn(Schedulers.io())

        addDispose(
            Observable.zip(banner, articleList,
                BiFunction<BaseResponse<MutableList<BannerModel>>,
                        BaseResponse<ArticleListModel>,
                        MutableList<MultiItemEntity>> { list: BaseResponse<MutableList<BannerModel>>, articleListModel: BaseResponse<ArticleListModel> ->
                    val result = mutableListOf<MultiItemEntity>()
                    result.add(MultipleBannerModel(list.data))
                    val model = articleListModel.data
                    model?.let {
                        page = it.curPage
                        if (it.over) {
                            loadEnd.postValue(it.over)
                        }
                    }
                    model?.datas?.let { result.addAll(it) }
                    result
                }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    mutableData.value = it
                }, {
                    it.message?.let { msg ->
                        toast(msg)
                    }
                })
        )
    }

    /**
     * 请求更多文章
     */
    fun loadArticle() {
        addDispose(
            indexRepository.getArticleListObservable(
                page,
                object : RequestObserver<ArticleListModel>() {
                    override fun onSuccess(data: ArticleListModel?) {
                        super.onSuccess(data)
                        data?.apply {
                            page = curPage
                            articleData.value = datas
                            if (over) {
                                loadEnd.value = over
                            }
                        }
                    }
                })
        )
    }

    /**
     * 取消收藏文章
     *
     * @param model 文章数据
     * @param position  下标
     */
    fun unCollect(model: ArticleModel, position: Int) {
        addDispose(indexRepository.unCollect(model.id, object : RequestObserver<CommonModel>() {
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
        addDispose(indexRepository.collect(model.id, object : RequestObserver<CommonModel>() {
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