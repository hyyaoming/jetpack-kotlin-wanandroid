package org.lym.wanandroid_kotlin.data.db.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * 历史搜索
 *
 * author: liyaoming
 * date: 2020-01-04-11:29
 */
@Entity(tableName = "words")
data class KeyWord(
    @PrimaryKey()
    val wordName: String
)
