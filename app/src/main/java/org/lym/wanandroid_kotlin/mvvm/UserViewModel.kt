package org.lym.wanandroid_kotlin.mvvm

import androidx.lifecycle.MutableLiveData
import org.lym.wanandroid_kotlin.data.RequestObserver
import org.lym.wanandroid_kotlin.data.model.LoginModel
import org.lym.wanandroid_kotlin.data.repository.UserRepository
import org.lym.wanandroid_kotlin.utils.toast

/**
 * 用户中心ViewModel
 *
 * author: liyaoming
 * email: liyaoming@bixin.cn
 * date: 2020-01-03-14:03
 */
class UserViewModel(private val repository: UserRepository) : AutoDisposeViewModel() {
    val loginModel = MutableLiveData<LoginModel>()

    /**
     * 用户登陆
     *
     * @param username
     * @param password
     */
    fun login(username: String, password: String) {
        repository.login(username, password, object : RequestObserver<LoginModel>() {
            override fun onSuccess(data: LoginModel?) {
                super.onSuccess(data)
                data?.apply {
                    loginModel.value = this
                }
            }

            override fun onFailed(code: Int, msg: String) {
                super.onFailed(code, msg)
                toast(msg)
            }
        })
    }


}