package org.lym.wanandroid_kotlin.data.model

import com.chad.library.adapter.base.entity.MultiItemEntity

/**
 * author: liyaoming
 * email: liyaoming@bixin.cn
 * date: 2020-01-02-15:42
 */
open class MultipleEntity(
    override val itemType: Int
) : MultiItemEntity {

    companion object {
        /**
         * banner
         */
        const val BANNER = 1
        /**
         * 文章
         */
        const val ARTICLE = 2
    }
}