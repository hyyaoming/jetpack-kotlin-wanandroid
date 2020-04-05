package org.lym.wanandroid_kotlin.weight

import android.view.View
import android.view.ViewGroup
import com.chad.library.adapter.base.loadmore.BaseLoadMoreView
import com.chad.library.adapter.base.util.getItemView
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import org.lym.wanandroid_kotlin.R

/**
 * author: liyaoming
 * date: 2020-01-03-20:46
 */
class LoadMoreView : BaseLoadMoreView() {

    override fun getRootView(parent: ViewGroup): View =
        parent.getItemView(R.layout.view_load_more)

    override fun getLoadingView(holder: BaseViewHolder): View =
        holder.getView(R.id.load_more_loading_view)

    override fun getLoadComplete(holder: BaseViewHolder): View =
        holder.getView(R.id.load_more_load_complete_view)

    override fun getLoadEndView(holder: BaseViewHolder): View =
        holder.getView(R.id.load_more_load_end_view)

    override fun getLoadFailView(holder: BaseViewHolder): View =
        holder.getView(R.id.load_more_load_fail_view)
}

object LoadMore {

    /**
     * 设置全局的LodeMoreView
     */
    @JvmStatic
    var loadMoreView: BaseLoadMoreView = LoadMoreView()
}