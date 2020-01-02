package org.lym.wanandroid_kotlin

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.Navigation
import androidx.navigation.ui.NavigationUI
import com.google.android.material.bottomnavigation.BottomNavigationView
import org.lym.wanandroid_kotlin.R


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val navController = Navigation.findNavController(this,
            R.id.nav_main_host
        )
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_menu)
        NavigationUI.setupWithNavController(bottomNav, navController)
    }
}
