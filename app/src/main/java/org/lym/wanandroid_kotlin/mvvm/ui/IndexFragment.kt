package org.lym.wanandroid_kotlin.mvvm.ui

import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.index_fragment.*
import org.lym.wanandroid_kotlin.R
import org.lym.wanandroid_kotlin.data.model.ARTICLE
import org.lym.wanandroid_kotlin.data.repository.IndexRepository
import org.lym.wanandroid_kotlin.mvvm.IndexViewModel
import org.lym.wanandroid_kotlin.mvvm.ViewModelFactory
import org.lym.wanandroid_kotlin.mvvm.adapter.ArticleAdapter
import org.lym.wanandroid_kotlin.utils.toast
import org.lym.wanandroid_kotlin.weight.LoadMore
import org.lym.wanandroid_kotlin.data.model.ArticleModel as ArticleModel1

/**
 * 首页
 *
 * author: liyaoming
 * email: liyaoming@bixin.cn
 * date: 2020-01-02-11:20
 */
class IndexFragment : BaseFragment() {
    private val indexViewModel: IndexViewModel by viewModels {
        ViewModelFactory(IndexRepository.getInstance())
    }

    private val adapter by lazy {
        ArticleAdapter()
    }

    override fun getLayoutResource(): Int {
        return R.layout.index_fragment
    }

    override fun subscribeUI() {
        indexViewModel.mutableData.observe(this, Observer {
            adapter.setNewData(it)
        })

        indexViewModel.articleData.observe(this, Observer {
            adapter.addData(it)
            adapter.loadMoreModule?.loadMoreComplete()
        })

        indexViewModel.loadEnd.observe(this, Observer {
            adapter.loadMoreModule?.loadMoreEnd(it)
        })

        indexViewModel.articleCollect.observe(this, Observer {
            adapter.notifyItemChanged(it)
        })
    }

    override fun initView() {
        rv_index.layoutManager = activity?.let {
            LinearLayoutManager(it).apply {
                orientation = LinearLayoutManager.VERTICAL
            }
        }
        rv_index.setHasFixedSize(true)
        rv_index.adapter = adapter

        adapter.setOnItemClickListener { adapter, _, position ->
            if (adapter.getItemViewType(position) == ARTICLE) {
                val item = adapter.getItem(position) as ArticleModel1
                toast(item.chapterName)
            }
        }

        adapter.setOnItemChildClickListener { _, view, position ->
            if (view.id == R.id.v_collect) {
                val articleModel = adapter.getItem(position) as ArticleModel1
                if (articleModel.collect) {
                    indexViewModel.unCollect(articleModel, position)
                } else {
                    indexViewModel.collect(articleModel, position)
                }
            }
        }

        adapter.loadMoreModule?.loadMoreView = LoadMore.loadMoreView
        adapter.loadMoreModule?.preLoadNumber = 0
        adapter.loadMoreModule?.setOnLoadMoreListener {
            indexViewModel.loadArticle()
        }

    }
}