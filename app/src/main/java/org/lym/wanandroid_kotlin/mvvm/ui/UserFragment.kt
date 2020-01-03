package org.lym.wanandroid_kotlin.mvvm.ui

import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import kotlinx.android.synthetic.main.user_fragment.*
import org.lym.wanandroid_kotlin.R
import org.lym.wanandroid_kotlin.data.repository.UserRepository
import org.lym.wanandroid_kotlin.mvvm.UserViewModel
import org.lym.wanandroid_kotlin.mvvm.ViewModelFactory
import org.lym.wanandroid_kotlin.utils.toast

/**
 * 用户中心fragment
 *
 * author: liyaoming
 * email: liyaoming@bixin.cn
 * date: 2020-01-02-13:04
 */
class UserFragment : BaseFragment() {
    private val viewModel: UserViewModel by viewModels {
        ViewModelFactory(UserRepository.getInstance())
    }

    override fun initView() {

    }

    override fun subscribeUI() {
        viewModel.loginModel.observe(this, Observer {
            toast("登陆成功")
        })
    }

    override fun getLayoutResource(): Int {
        return R.layout.user_fragment
    }

}