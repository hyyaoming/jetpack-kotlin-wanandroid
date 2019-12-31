package org.lym.wanandroid_kotlin.data.model

/**
 * author: liyaoming
 * email: liyaoming@bixin.cn
 * date: 2019-12-31-13:46
 */
data class BannerModel(
    var desc: String,
    var id: Int,
    var imagePath: String,
    var isVisible: Int,
    var order: Int, var type: Int, var url: String
)