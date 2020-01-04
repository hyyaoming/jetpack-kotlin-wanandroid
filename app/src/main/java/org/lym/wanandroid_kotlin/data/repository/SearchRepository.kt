package org.lym.wanandroid_kotlin.data.repository

import androidx.lifecycle.LiveData
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.lym.wanandroid_kotlin.data.RequestObserver
import org.lym.wanandroid_kotlin.data.db.KeyWordDao
import org.lym.wanandroid_kotlin.data.db.model.KeyWord
import org.lym.wanandroid_kotlin.data.model.HotWordModel
import org.lym.wanandroid_kotlin.http.HttpRequest
import org.lym.wanandroid_kotlin.http.getApiService

/**
 * 搜索仓库
 *
 * author: liyaoming
 * email: liyaoming@bixin.cn
 * date: 2020-01-04-18:01
 */
class SearchRepository private constructor(private val wordDao: KeyWordDao) : Repository {

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
    fun insertSearchWord(word: String) {
        Observable.create<Any> {
            wordDao.insertWord(KeyWord(word))
        }.subscribeOn(Schedulers.io())
            .subscribe()
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