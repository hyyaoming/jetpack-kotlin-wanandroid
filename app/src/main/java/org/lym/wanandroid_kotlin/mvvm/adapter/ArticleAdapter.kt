package org.lym.wanandroid_kotlin.mvvm.adapter

import android.widget.ImageView
import androidx.viewpager2.widget.ViewPager2
import com.chad.library.adapter.base.BaseMultiItemQuickAdapter
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.entity.MultiItemEntity
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import org.lym.wanandroid_kotlin.R
import org.lym.wanandroid_kotlin.data.model.*
import org.lym.wanandroid_kotlin.http.glide.ImageLoader
import org.lym.wanandroid_kotlin.utils.toast

/**
 * 首页adapter
 *
 * author: liyaoming
 * email: liyaoming@bixin.cn
 * date: 2019-12-31-15:45
 */
class ArticleAdapter : BaseMultiItemQuickAdapter<MultiItemEntity, BaseViewHolder>() {

    init {
        addItemType(BANNER, R.layout.item_banner_layout)
        addItemType(ARTICLE, R.layout.cell_aericle_layout)
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
        helper.setText(R.id.tv_nice_time, item.niceDate)
        helper.setGone(R.id.tv_new_label, !item.fresh)
        helper.setText(R.id.tv_author, item.getUser())
        helper.setText(R.id.tv_article_title, item.title)
    }

    private fun bindBanner(
        item: MultiItemEntity?,
        helper: BaseViewHolder
    ) {
        val bannerModel = item as MultipleBannerModel
        val viewPager2 = helper.itemView as ViewPager2
        viewPager2.adapter =
            object : BaseQuickAdapter<BannerModel, BaseViewHolder>(
                R.layout.banner_item_layout,
                bannerModel.banners
            ) {
                override fun convert(helper: BaseViewHolder, item: BannerModel?) {
                    ImageLoader.banner(helper.itemView as ImageView, item?.imagePath)
                }
            }.apply {
                setOnItemClickListener { _, _, position ->
                    val model = getItem(position)
                    toast(model?.url)
                }
            }
    }

}