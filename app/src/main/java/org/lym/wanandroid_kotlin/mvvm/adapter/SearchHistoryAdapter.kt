package org.lym.wanandroid_kotlin.mvvm.adapter

import android.text.Html
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import androidx.core.content.ContextCompat
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
    BaseQuickAdapter<ArticleModel, BaseViewHolder>(R.layout.cell_hot_article_lyaout) {
    override fun convert(helper: BaseViewHolder, item: ArticleModel?) {
        item?.let {
            helper.setText(R.id.tv_article_top, "Top${helper.adapterPosition}")
            helper.setText(R.id.tv_article_author, it.getUser())
            helper.setText(R.id.tv_article_title, it.title)
            helper.setText(R.id.tv_article_desc, Html.fromHtml(it.desc))
            helper.setGone(R.id.tv_article_desc, it.desc.isEmpty())
            helper.setText(R.id.tv_article_nice_time, it.niceDate)
            val builder =
                SpannableStringBuilder("${it.superChapterName}~${it.chapterName}")
            builder.setSpan(
                ForegroundColorSpan(ContextCompat.getColor(context, R.color.color_333333)),
                0,
                it.chapterName.length + 1,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            helper.setText(R.id.tvChapterName, builder)
        }
    }
}