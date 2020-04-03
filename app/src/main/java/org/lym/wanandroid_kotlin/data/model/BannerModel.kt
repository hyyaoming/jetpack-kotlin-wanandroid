package org.lym.wanandroid_kotlin.data.model

import com.chad.library.adapter.base.entity.MultiItemEntity

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
    var order: Int, var type: Int, var url: String,var title : String
)

data class MultipleBannerModel(
    var banners: MutableList<BannerModel>?
) : MultiItemEntity {
    override val itemType: Int
        get() = BANNER
}