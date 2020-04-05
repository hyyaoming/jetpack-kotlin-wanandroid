package org.lym.wanandroid_kotlin.mvvm.ui.main

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.chad.library.adapter.base.BaseQuickAdapter
import kotlinx.android.synthetic.main.fragment_index.*
import org.lym.wanandroid_kotlin.R
import org.lym.wanandroid_kotlin.common.WEB_TITLE
import org.lym.wanandroid_kotlin.common.WEB_URL
import org.lym.wanandroid_kotlin.data.model.ARTICLE
import org.lym.wanandroid_kotlin.data.repository.IndexRepository
import org.lym.wanandroid_kotlin.mvvm.ViewModelFactory
import org.lym.wanandroid_kotlin.mvvm.adapter.ArticleAdapter
import org.lym.wanandroid_kotlin.mvvm.ui.BaseFragment
import org.lym.wanandroid_kotlin.mvvm.ui.WebActivity
import org.lym.wanandroid_kotlin.mvvm.ui.gank.GankMeiZhiActivity
import org.lym.wanandroid_kotlin.mvvm.ui.search.SearchActivity
import org.lym.wanandroid_kotlin.mvvm.viewmodel.AutoDisposeViewModel
import org.lym.wanandroid_kotlin.mvvm.viewmodel.IndexViewModel
import org.lym.wanandroid_kotlin.data.model.ArticleModel as ArticleModel1

/**
 * 首页
 *
 * author: liyaoming
 * date: 2020-01-02-11:20
 */
class IndexFragment : BaseFragment() {
    private val indexViewModel: IndexViewModel by viewModels {
        ViewModelFactory(IndexRepository.getInstance())
    }

    private val adapter by lazy {
        ArticleAdapter()
    }

    override fun getAdapter(): BaseQuickAdapter<*, *>? {
        return adapter
    }

    override fun getViewModel(): AutoDisposeViewModel? {
        return indexViewModel
    }

    override fun getLayoutResource() = R.layout.fragment_index

    override fun subscribeUI() {
        indexViewModel.mutableData.observe(this, Observer {
            adapter.setDiffNewData(it)
        })

        indexViewModel.articleData.observe(this, Observer {
            adapter.addData(it)
        })

        indexViewModel.articleCollect.observe(this, Observer {
            adapter.notifyItemChanged(it)
        })
    }

    override fun getRecyclerView(): RecyclerView? {
        return rv_index
    }

    override fun initView() {
        rv_index.layoutManager = LinearLayoutManager(requireContext())
        rv_index.setHasFixedSize(true)
        rv_index.adapter = adapter

        title_bar.setOnLeftIconClickListener {
            startActivity(Intent(requireActivity(), GankMeiZhiActivity::class.java))
        }

        title_bar.setOnRightIconClickListener {
            startActivity(Intent(requireActivity(), SearchActivity::class.java))
            requireActivity().overridePendingTransition(R.anim.open_bottom_in, R.anim.open_top_out)
        }
    }

    override fun onItemClickListener(adapter: BaseQuickAdapter<*, *>, view: View, position: Int) {
        if (adapter.getItemViewType(position) == ARTICLE) {
            val item = adapter.getItem(position) as ArticleModel1
            startActivity(Intent(activity, WebActivity::class.java).apply {
                putExtras(Bundle().apply {
                    putString(WEB_URL, item.link)
                    putString(WEB_TITLE, item.title)
                })
            })
        }
    }

    override fun onItemChildClickListener(
        adapter: BaseQuickAdapter<*, *>,
        view: View,
        position: Int
    ) {
        if (view.id == R.id.v_collect) {
            val articleModel = adapter.getItem(position) as ArticleModel1
            if (articleModel.collect) {
                indexViewModel.unCollect(articleModel, position)
            } else {
                indexViewModel.collect(articleModel, position)
            }
        }

    }

    override fun loadMore() {
        indexViewModel.loadArticle()
    }
}