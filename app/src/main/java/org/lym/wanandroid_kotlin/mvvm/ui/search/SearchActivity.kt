package org.lym.wanandroid_kotlin.mvvm.ui.search

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import org.lym.wanandroid_kotlin.R

/**
 * 搜索页面
 *
 * author: liyaoming
 * email: liyaoming@bixin.cn
 * date: 2020-01-04-16:58
 */
class SearchActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(
            R.anim.close_top_in,
            R.anim.close_bottom_out
        )
    }
}
