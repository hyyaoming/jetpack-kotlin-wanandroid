package org.lym.wanandroid_kotlin.mvvm.ui.search

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.plusAssign
import org.lym.wanandroid_kotlin.R
import org.lym.wanandroid_kotlin.weight.KeepStateNavigator

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
        val navController = findNavController(R.id.nav_search_host_fragment)
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_search_host_fragment)!!

        // setup custom navigator
        val navigator =
            KeepStateNavigator(
                this,
                navHostFragment.childFragmentManager,
                R.id.nav_search_host_fragment
            )
        navController.navigatorProvider += navigator

        // set navigation graph
        navController.setGraph(R.navigation.search_nav)
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(
            R.anim.close_top_in,
            R.anim.close_bottom_out
        )
    }
}
