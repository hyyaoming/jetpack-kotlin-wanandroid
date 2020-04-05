package org.lym.wanandroid_kotlin.mvvm.ui.search

import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.chad.library.adapter.base.BaseQuickAdapter
import kotlinx.android.synthetic.main.fragment_search_result.*
import org.lym.wanandroid_kotlin.R
import org.lym.wanandroid_kotlin.data.db.AppDataBase
import org.lym.wanandroid_kotlin.data.model.ArticleModel
import org.lym.wanandroid_kotlin.data.repository.SearchRepository
import org.lym.wanandroid_kotlin.mvvm.ViewModelFactory
import org.lym.wanandroid_kotlin.mvvm.adapter.SearchResultAdapter
import org.lym.wanandroid_kotlin.mvvm.ui.BaseFragment
import org.lym.wanandroid_kotlin.mvvm.viewmodel.AutoDisposeViewModel
import org.lym.wanandroid_kotlin.mvvm.viewmodel.SearchResultViewModel
import org.lym.wanandroid_kotlin.utils.hide
import org.lym.wanandroid_kotlin.utils.toast
import per.goweii.actionbarex.common.ActionBarSearch

/**
 * 搜索结果fragment
 *
 * author: liyaoming
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
            adapter.setNewData(data)
        })

        viewModel.searchResultMore.observe(this, Observer { data ->
            adapter.addData(data)
        })

        viewModel.articleCollect.observe(this, Observer {
            adapter.notifyItemChanged(it)
        })
    }

    override fun initView() {
        rv_search_result.layoutManager = LinearLayoutManager(requireContext())
        rv_search_result.setHasFixedSize(true)

        rv_search_result.adapter = adapter

        rv_search_result.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                hide(requireActivity().findViewById<ActionBarSearch>(R.id.search_view).editTextView)
            }
        })
    }

    override fun onItemClickListener(adapter: BaseQuickAdapter<*, *>, view: View, position: Int) {
        val item = adapter.getItem(position) as ArticleModel
        toast(item.chapterName)
    }

    override fun onItemChildClickListener(
        adapter: BaseQuickAdapter<*, *>,
        view: View,
        position: Int
    ) {
        if (view.id == R.id.rv_result_collect) {
            val articleModel = adapter.getItem(position) as ArticleModel
            if (articleModel.collect) {
                viewModel.unCollect(articleModel, position)
            } else {
                viewModel.collect(articleModel, position)
            }
        }
    }

    override fun getViewModel(): AutoDisposeViewModel? {
        return viewModel
    }

    override fun getAdapter(): BaseQuickAdapter<*, *>? {
        return adapter
    }

    override fun loadMore() {
        viewModel.loadMore()
    }
}