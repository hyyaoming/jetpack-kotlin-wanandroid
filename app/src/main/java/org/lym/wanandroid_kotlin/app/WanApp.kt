package org.lym.wanandroid_kotlin.app

import android.app.Application
import android.content.Context

/**
 * author: liyaoming
 * date: 2019-12-31-15:46
 */
class WanApp : Application() {

    override fun onCreate() {
        super.onCreate()
        context = this
    }

    companion object {
        private lateinit var context: Application

        fun getContext(): Context {
            return context
        }
    }

}