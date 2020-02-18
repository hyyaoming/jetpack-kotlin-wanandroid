package org.lym.wanandroid_kotlin.mvvm.adapter.diff

import androidx.recyclerview.widget.DiffUtil
import com.chad.library.adapter.base.entity.MultiItemEntity
import org.lym.wanandroid_kotlin.data.model.ARTICLE
import org.lym.wanandroid_kotlin.data.model.ArticleModel
import org.lym.wanandroid_kotlin.data.model.BANNER
import org.lym.wanandroid_kotlin.data.model.MultipleBannerModel

/**
 * 首页Item Differ
 *
 * author: liyaoming
 * email: liyaoming@bixin.cn
 * date: 2020-02-18-10:59
 */
class IndexDiffer : DiffUtil.ItemCallback<MultiItemEntity>() {
    override fun areItemsTheSame(oldItem: MultiItemEntity, newItem: MultiItemEntity): Boolean {
        if (bannerItem(oldItem, newItem)) {
            return differBanner(oldItem as MultipleBannerModel, newItem as MultipleBannerModel)
        }
        if (articleItem(oldItem, newItem)) {
            return differArticle(oldItem as ArticleModel, newItem as ArticleModel)
        }
        return false
    }

    override fun areContentsTheSame(oldItem: MultiItemEntity, newItem: MultiItemEntity): Boolean {
        if (bannerItem(oldItem, newItem)) {
            return (oldItem as MultipleBannerModel).banners?.size == (newItem as MultipleBannerModel).banners?.size
        }
        if (articleItem(oldItem, newItem)) {
            return (oldItem as ArticleModel).id == (newItem as ArticleModel).id
        }
        return false
    }

    private fun articleItem(
        oldItem: MultiItemEntity,
        newItem: MultiItemEntity
    ) = oldItem.itemType == ARTICLE && newItem.itemType == ARTICLE

    private fun bannerItem(
        oldItem: MultiItemEntity,
        newItem: MultiItemEntity
    ) = oldItem.itemType == BANNER && newItem.itemType == BANNER

    private fun differArticle(oldItem: ArticleModel, newItem: ArticleModel): Boolean {
        return oldItem.id == newItem.id && oldItem.chapterName == newItem.chapterName
    }

    private fun differBanner(oldItem: MultipleBannerModel, newItem: MultipleBannerModel): Boolean {
        val oldBanners = oldItem.banners
        val newBanners = newItem.banners
        if (oldBanners?.size != newBanners?.size) {
            return false
        }
        oldBanners?.forEachIndexed { index, bannerModel ->
            if (bannerModel.id != newBanners?.get(index)?.id) {
                return false
            }
        }
        return true
    }
}