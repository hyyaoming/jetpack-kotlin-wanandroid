package org.lym.wanandroid_kotlin.mvvm.adapter

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import org.lym.wanandroid_kotlin.R
import org.lym.wanandroid_kotlin.data.model.ArticleModel

/**
 * 搜索历史adapter
 *
 * author: liyaoming
 * email: liyaoming@bixin.cn
 * date: 2020-01-04-19:24
 */
class SearchHistoryAdapter :
    BaseQuickAdapter<ArticleModel, BaseViewHolder>(R.layout.cell_hot_article_lyaout, null) {
    override fun convert(helper: BaseViewHolder, item: ArticleModel?) {
    }
}