package org.lym.wanandroid_kotlin.mvvm.adapter

import android.text.Html
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import androidx.core.content.ContextCompat
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.module.LoadMoreModule
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import org.lym.wanandroid_kotlin.R
import org.lym.wanandroid_kotlin.data.model.ArticleModel
import org.lym.wanandroid_kotlin.weight.LoadMore

/**
 * 搜索结果
 *
 * author: liyaoming
 * date: 2020-01-06-17:16
 */
class SearchResultAdapter :
    BaseQuickAdapter<ArticleModel, BaseViewHolder>(R.layout.cell_search_result_layout),
    LoadMoreModule {
    override fun convert(helper: BaseViewHolder, item: ArticleModel?) {
        item?.apply {
            helper.setText(R.id.tv_result_author, item.getUser())
            helper.setText(R.id.tv_result_nice_time, item.niceDate)
            helper.setText(R.id.tv_result_title, Html.fromHtml(item.title))
            helper.setText(R.id.tv_result_desc, Html.fromHtml(item.desc))
            helper.setText(R.id.tv_result_author, item.getUser())
            helper.setGone(R.id.tv_result_desc, desc.isEmpty())
            val builder =
                SpannableStringBuilder("${superChapterName}~${chapterName}")
            builder.setSpan(
                ForegroundColorSpan(ContextCompat.getColor(context, R.color.color_333333)),
                0,
                chapterName.length + 1,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            helper.setText(R.id.tv_result_ChapterName, builder)
            helper.setImageResource(
                R.id.rv_result_collect,
                if (collect) R.drawable.collect_svg_icon else R.drawable.no_collect_svg_icon
            )
        }
        addChildClickViewIds(R.id.rv_result_collect)
    }
}