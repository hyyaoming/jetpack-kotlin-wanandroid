package org.lym.wanandroid_kotlin.data.db

import androidx.lifecycle.LiveData
import androidx.room.*
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
    @Query("SELECT * FROM words ORDER BY wordName asc LIMIT 10")
    fun getWords(): LiveData<List<KeyWord>>

    /**
     * 插入一条搜索记录
     *
     * @param word  搜索记录
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertWord(word: KeyWord)

    /**
     * 删除历史搜索记录
     *
     * @param words 历史记录
     */
    @Delete
    fun deleteHistory(words: List<KeyWord>)
}