package org.lym.wanandroid_kotlin.mvvm.adapter

import android.content.Intent
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.viewpager2.widget.ViewPager2
import com.chad.library.adapter.base.BaseMultiItemQuickAdapter
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.entity.MultiItemEntity
import com.chad.library.adapter.base.module.LoadMoreModule
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import org.lym.wanandroid_kotlin.R
import org.lym.wanandroid_kotlin.common.WEB_TITLE
import org.lym.wanandroid_kotlin.common.WEB_URL
import org.lym.wanandroid_kotlin.data.model.*
import org.lym.wanandroid_kotlin.http.glide.GlideLoader
import org.lym.wanandroid_kotlin.mvvm.adapter.diff.IndexDiffer
import org.lym.wanandroid_kotlin.mvvm.ui.WebActivity

/**
 * 首页adapter
 *
 * author: liyaoming
 * date: 2019-12-31-15:45
 */
class ArticleAdapter : BaseMultiItemQuickAdapter<MultiItemEntity, BaseViewHolder>(),
    LoadMoreModule {

    init {
        addItemType(BANNER, R.layout.cell_banner_layout)
        addItemType(ARTICLE, R.layout.cell_aericle_layout)
        setDiffCallback(IndexDiffer())
    }

    override fun convert(helper: BaseViewHolder, item: MultiItemEntity?) {
        when (helper.itemViewType) {
            BANNER -> {
                bindBanner(item, helper)
            }
            ARTICLE -> {
                bindArticleItem(item as ArticleModel, helper)
            }
        }
    }

    private fun bindArticleItem(item: ArticleModel, helper: BaseViewHolder) {
        addChildClickViewIds(R.id.v_collect)
        helper.setText(R.id.tv_nice_time, item.niceDate)
        helper.setGone(R.id.tv_new_label, !item.fresh)
        helper.setText(R.id.tv_author, item.getUser())
        helper.setText(R.id.tv_article_title, item.title)
        helper.setImageResource(
            R.id.iv_collect,
            if (item.collect) R.drawable.collect_svg_icon else R.drawable.no_collect_svg_icon
        )
        val builder =
            SpannableStringBuilder("${item.superChapterName}~${item.chapterName}")
        builder.setSpan(
            ForegroundColorSpan(ContextCompat.getColor(context, R.color.color_333333)),
            0,
            item.chapterName.length + 1,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        helper.setText(R.id.tvChapterName, builder)
    }

    private fun bindBanner(
        item: MultiItemEntity?,
        helper: BaseViewHolder
    ) {
        val bannerModel = item as MultipleBannerModel
        val viewPager2 = helper.itemView as ViewPager2
        viewPager2.adapter =
            object : BaseQuickAdapter<BannerModel, BaseViewHolder>(
                R.layout.cell_banner_cell_layout,
                bannerModel.banners
            ) {
                override fun convert(helper: BaseViewHolder, item: BannerModel?) {
                    GlideLoader.image(helper.itemView as ImageView, item?.imagePath)
                }
            }.apply {
                setOnItemClickListener { _, _, position ->
                    val model = getItem(position)
                    context.startActivity(Intent(context, WebActivity::class.java).apply {
                        putExtras(Bundle().apply {
                            putString(WEB_URL, model?.url)
                            putString(WEB_TITLE, model?.title)
                        })
                    })
                }
            }
    }

}
