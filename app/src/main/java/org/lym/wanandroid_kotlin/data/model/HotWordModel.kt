package org.lym.wanandroid_kotlin.data.model

/**
 * 热词
 *
 * author: liyaoming
 * date: 2020-01-04-11:14
 */
data class HotWordModel(
    var id: Int,
    var link: String,
    var name: String,
    var order: Int,
    var visible: Int
)