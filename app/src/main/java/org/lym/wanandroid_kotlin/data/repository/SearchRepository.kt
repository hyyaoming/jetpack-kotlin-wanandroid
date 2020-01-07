package org.lym.wanandroid_kotlin.data.repository

import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import org.lym.wanandroid_kotlin.data.RequestObserver
import org.lym.wanandroid_kotlin.data.db.KeyWordDao
import org.lym.wanandroid_kotlin.data.db.model.KeyWord
import org.lym.wanandroid_kotlin.data.model.HotWordModel
import org.lym.wanandroid_kotlin.http.HttpRequest
import org.lym.wanandroid_kotlin.http.exception.ApiException
import org.lym.wanandroid_kotlin.http.getApiService

/**
 * 搜索仓库
 *
 * author: liyaoming
 * email: liyaoming@bixin.cn
 * date: 2020-01-04-18:01
 */
class SearchRepository private constructor(private val wordDao: KeyWordDao) : ArticleRepository() {

    /**
     * 获取搜索热词
     * @param requestObserver   请求回调
     */
    fun getHotWork(requestObserver: RequestObserver<MutableList<HotWordModel>>) =
        HttpRequest.create(getApiService().hotWork()).request(requestObserver)

    /**
     * 获取历史搜索记录
     */
    fun getHistoryWords() = wordDao.getWords()

    /**
     * 插入一条搜索记录
     *
     * @param word  搜索关键字
     */
    fun insertSearchWord(word: String): Disposable =
        Observable.create<Any> {
            wordDao.insertWord(KeyWord(word))
        }.subscribeOn(Schedulers.io())
            .subscribe()


    /**
     * 删除历史搜索记录
     *
     * @param words 历史搜索关键字
     * @param requestObserver   回调
     * @return  返回dispose
     */
    fun deleteWords(
        words: List<KeyWord>,
        requestObserver: RequestObserver<Boolean>
    ): Disposable {
        return Observable.create<Any> {
            wordDao.deleteHistory(words)
        }.subscribeOn(Schedulers.io())
            .subscribe({
                requestObserver.onSuccess(true)
            }, {
                requestObserver.onFailed(0, "error")
            })
    }

    companion object {

        private var instance: SearchRepository? = null

        fun getInstance(wordDao: KeyWordDao) =
            instance ?: synchronized(this) {
                SearchRepository(wordDao).also {
                    instance = it
                }
            }
    }

}