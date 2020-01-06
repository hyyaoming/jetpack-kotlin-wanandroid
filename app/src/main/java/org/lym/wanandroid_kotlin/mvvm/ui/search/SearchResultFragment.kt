package org.lym.wanandroid_kotlin.mvvm.ui.search

import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.chad.library.adapter.base.BaseQuickAdapter
import kotlinx.android.synthetic.main.fragment_search_result.*
import org.lym.wanandroid_kotlin.R
import org.lym.wanandroid_kotlin.data.db.AppDataBase
import org.lym.wanandroid_kotlin.data.model.ARTICLE
import org.lym.wanandroid_kotlin.data.model.ArticleModel
import org.lym.wanandroid_kotlin.data.repository.SearchRepository
import org.lym.wanandroid_kotlin.mvvm.ViewModelFactory
import org.lym.wanandroid_kotlin.mvvm.adapter.SearchResultAdapter
import org.lym.wanandroid_kotlin.mvvm.ui.BaseFragment
import org.lym.wanandroid_kotlin.mvvm.viewmodel.AutoDisposeViewModel
import org.lym.wanandroid_kotlin.mvvm.viewmodel.SearchResultViewModel
import org.lym.wanandroid_kotlin.utils.toast
import org.lym.wanandroid_kotlin.weight.LoadMore

/**
 * 搜索结果fragment
 *
 * author: liyaoming
 * email: liyaoming@bixin.cn
 * date: 2020-01-04-17:11
 */
class SearchResultFragment : BaseFragment() {

    private val viewModel: SearchResultViewModel by viewModels {
        ViewModelFactory(SearchRepository.getInstance(AppDataBase.getInstance().keyWordsDao()))
    }

    private val adapter: SearchResultAdapter by lazy {
        SearchResultAdapter()
    }
    private val args: HistoryFragmentArgs by navArgs()

    override fun getLayoutResource() = R.layout.fragment_search_result

    override fun subscribeUI() {
        val key = args.searchKey
        viewModel.searchArticle(key)
        viewModel.searchResult.observe(this, Observer { data ->
            adapter.addData(data)
            adapter.loadMoreModule?.loadMoreComplete()
        })


        viewModel.loadEnd.observe(this, Observer {
            adapter.loadMoreModule?.loadMoreEnd(it)
        })

        viewModel.articleCollect.observe(this, Observer {
            adapter.notifyItemChanged(it)
        })
    }

    override fun initView() {
        rv_search_result.layoutManager = LinearLayoutManager(requireContext())
        rv_search_result.setHasFixedSize(true)

        rv_search_result.adapter = adapter

        adapter.setOnItemClickListener { adapter, _, position ->
            val item = adapter.getItem(position) as ArticleModel
            toast(item.chapterName)
        }

        adapter.setOnItemChildClickListener { _, view, position ->
            if (view.id == R.id.rv_result_collect) {
                val articleModel = adapter.getItem(position) as ArticleModel
                if (articleModel.collect) {
                    viewModel.unCollect(articleModel, position)
                } else {
                    viewModel.collect(articleModel, position)
                }
            }
        }

        adapter.loadMoreModule?.loadMoreView = LoadMore.loadMoreView
        adapter.loadMoreModule?.preLoadNumber = 0
        adapter.loadMoreModule?.setOnLoadMoreListener {
            viewModel.loadMore()
        }


    }
}