package org.lym.wanandroid_kotlin.mvvm.adapter

import com.chad.library.adapter.base.BaseMultiItemQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import org.lym.wanandroid_kotlin.R
import org.lym.wanandroid_kotlin.data.model.MultipleEntity
import org.lym.wanandroid_kotlin.data.model.MultipleEntity.Companion.ARTICLE
import org.lym.wanandroid_kotlin.data.model.MultipleEntity.Companion.BANNER

/**
 * 首页adapter
 *
 * author: liyaoming
 * email: liyaoming@bixin.cn
 * date: 2019-12-31-15:45
 */
class ArticleAdapter : BaseMultiItemQuickAdapter<MultipleEntity, BaseViewHolder>() {

    init {
        addItemType(BANNER, R.layout.banner_item_layout)
        addItemType(ARTICLE, R.layout.banner_item_layout)
    }

    override fun convert(helper: BaseViewHolder, item: MultipleEntity?) {
        when (helper.itemViewType) {
            BANNER -> {

            }
            ARTICLE -> {
            }
        }
    }

}
