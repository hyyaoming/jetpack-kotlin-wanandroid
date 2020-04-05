package org.lym.wanandroid_kotlin.mvvm.ui.main

import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import kotlinx.android.synthetic.main.fragment_user.*
import org.lym.wanandroid_kotlin.R
import org.lym.wanandroid_kotlin.data.repository.UserRepository
import org.lym.wanandroid_kotlin.mvvm.viewmodel.UserViewModel
import org.lym.wanandroid_kotlin.mvvm.ViewModelFactory
import org.lym.wanandroid_kotlin.mvvm.ui.BaseFragment
import org.lym.wanandroid_kotlin.utils.toast

/**
 * 用户中心fragment
 *
 * author: liyaoming
 * date: 2020-01-02-13:04
 */
class UserFragment : BaseFragment() {
    private val viewModel: UserViewModel by viewModels {
        ViewModelFactory(UserRepository.getInstance())
    }

    override fun initView() {
        tv_username.setOnClickListener {
            viewModel.login("hyyaoming", "womenyiqiguo")
        }
    }

    override fun subscribeUI() {
        viewModel.loginModel.observe(this, Observer {
            toast("登陆成功")
        })
    }

    override fun getLayoutResource() = R.layout.fragment_user

}