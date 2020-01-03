package org.lym.wanandroid_kotlin

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.plusAssign
import androidx.navigation.ui.setupWithNavController
import kotlinx.android.synthetic.main.activity_main.*
import org.lym.wanandroid_kotlin.weight.KeepStateNavigator


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val navController = findNavController(R.id.nav_main_host)
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_main_host)!!

        // setup custom navigator
        val navigator =
            KeepStateNavigator(this, navHostFragment.childFragmentManager, R.id.nav_main_host)
        navController.navigatorProvider += navigator

        // set navigation graph
        navController.setGraph(R.navigation.main_nav)
        bottom_menu.setupWithNavController(navController)
    }
}
