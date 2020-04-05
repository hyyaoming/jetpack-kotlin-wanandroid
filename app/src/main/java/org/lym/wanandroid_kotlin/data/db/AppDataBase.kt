package org.lym.wanandroid_kotlin.data.db

import androidx.databinding.adapters.Converters
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import org.lym.wanandroid_kotlin.app.WanApp
import org.lym.wanandroid_kotlin.common.DATABASE_NAME
import org.lym.wanandroid_kotlin.data.db.model.KeyWord

/**
 * 本地数据库类
 *
 * author: liyaoming
 * date: 2020-01-04-11:32
 */
@Database(entities = [KeyWord::class], version = 1, exportSchema = false)
abstract class AppDataBase : RoomDatabase() {
    abstract fun keyWordsDao(): KeyWordDao

    companion object {

        private var instance: AppDataBase? = null

        fun getInstance(): AppDataBase {
            return instance ?: synchronized(this) {
                instance ?: buildDatabase().also {
                    instance = it
                }
            }
        }

        private fun buildDatabase(): AppDataBase {
            return Room
                .databaseBuilder(WanApp.getContext(), AppDataBase::class.java, DATABASE_NAME)
                .build()
        }

    }
}