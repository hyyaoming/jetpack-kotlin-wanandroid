package org.lym.wanandroid_kotlin.data.db

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import org.lym.wanandroid_kotlin.data.db.model.KeyWord

/**
 * 搜索历史数据库操作
 *
 * author: liyaoming
 * email: liyaoming@bixin.cn
 * date: 2020-01-04-11:38
 */
@Dao
interface KeyWordDao {
    /**
     * 查看历史搜索记录
     *
     * @return  返回历史搜索记录
     */
    @Query("SELECT * FROM words ORDER BY wordName LIMIT 8")
    fun getWords(): LiveData<List<KeyWord>>

    /**
     * 插入一条搜索记录
     *
     * @param word  搜索记录
     */
    @Insert
    fun insertWord(word: KeyWord)
}