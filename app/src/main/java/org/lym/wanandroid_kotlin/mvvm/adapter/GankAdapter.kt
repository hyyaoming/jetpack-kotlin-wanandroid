package org.lym.wanandroid_kotlin.mvvm.adapter

import android.widget.ImageView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.module.LoadMoreModule
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import org.lym.wanandroid_kotlin.R
import org.lym.wanandroid_kotlin.data.model.MeiZhi
import org.lym.wanandroid_kotlin.http.glide.GlideLoader
import org.lym.wanandroid_kotlin.utils.dip2px
import org.lym.wanandroid_kotlin.utils.getScreenWidth

/**
 * Gank.io adapter
 *
 * author: liyaoming
 * date: 2020-02-18-17:50
 */
class GankAdapter : BaseQuickAdapter<MeiZhi, BaseViewHolder>(R.layout.cell_gank_item),
    LoadMoreModule {

    private var cellWidth = 0

    init {
        cellWidth = (getScreenWidth() - dip2px(8F)) / 2
    }

    override fun convert(helper: BaseViewHolder, item: MeiZhi?) {
        GlideLoader.image(helper.itemView as ImageView, item?.httpsUrl())
        val params = helper.itemView.layoutParams
        params.width = cellWidth
        params.height = (cellWidth * 1.2F + 0.5F).toInt()
    }
}