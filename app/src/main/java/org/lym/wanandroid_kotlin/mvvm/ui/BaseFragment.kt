package org.lym.wanandroid_kotlin.mvvm.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import org.lym.wanandroid_kotlin.utils.view

/**
 * fragment基类
 *
 * author: liyaoming
 * email: liyaoming@bixin.cn
 * date: 2020-01-02-15:24
 */
abstract class BaseFragment : Fragment() {


    abstract fun getLayoutResource(): Int

    abstract fun subscribeUI()

    abstract fun initView()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        subscribeUI()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return activity?.view(getLayoutResource(), container, false)!!
    }
}