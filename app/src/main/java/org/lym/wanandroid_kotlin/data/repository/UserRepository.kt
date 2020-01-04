package org.lym.wanandroid_kotlin.data.repository

import org.lym.wanandroid_kotlin.data.RequestObserver
import org.lym.wanandroid_kotlin.data.model.LoginModel
import org.lym.wanandroid_kotlin.http.HttpRequest
import org.lym.wanandroid_kotlin.http.getApiService

/**
 * 用户中心数据来源
 *
 * author: liyaoming
 * email: liyaoming@bixin.cn
 * date: 2020-01-03-14:04
 */
class UserRepository : Repository {
    /**
     * 登陆请求
     *
     * @param username  用户名
     * @param password  密码
     * @param requestObserver   请求回调
     */
    fun login(username: String, password: String, requestObserver: RequestObserver<LoginModel>) {
        HttpRequest.create(getApiService().login(username, password))
            .request(requestObserver)
    }

    companion object {
        @Volatile
        private var instance: UserRepository? = null

        fun getInstance() =
            instance ?: synchronized(this) {
                instance ?: UserRepository().also {
                    instance = it
                }
            }
    }

}