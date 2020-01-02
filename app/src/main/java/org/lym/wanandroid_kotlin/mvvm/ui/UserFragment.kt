package org.lym.wanandroid_kotlin.mvvm.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import org.lym.wanandroid_kotlin.R

/**
 * author: liyaoming
 * email: liyaoming@bixin.cn
 * date: 2020-01-02-13:04
 */
class UserFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return LayoutInflater.from(activity).inflate(R.layout.user_fragment, container, false)
    }

}