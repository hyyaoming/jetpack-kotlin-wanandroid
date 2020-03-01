package org.lym.wanandroid_kotlin.data.model

/**
 * gank.io提供的妹纸
 *
 * author: liyaoming
 * email: liyaoming@bixin.cn
 * date: 2020-02-18-12:06
 */
data class GankMeiZhi(
    var results: MutableList<MeiZhi>? = null,
    var error: Boolean? = null
)

data class MeiZhi(
    var _id: String,
    var createdAt: String,
    var desc: String,
    var publishedAt: String,
    var source: String,
    var type: String,
    var url: String,
    var used: Boolean? = null,
    var who: String
) {

    fun httpsUrl(): String {
        var meiZhiUrl = url
        if (meiZhiUrl.contains("http://")) {
            meiZhiUrl = meiZhiUrl.replace("http://", "https://")
        }
        return meiZhiUrl
    }
}