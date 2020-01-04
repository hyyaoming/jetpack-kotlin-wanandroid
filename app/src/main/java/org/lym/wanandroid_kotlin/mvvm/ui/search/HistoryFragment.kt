package org.lym.wanandroid_kotlin.mvvm.ui.search

import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_search_history.*
import org.lym.wanandroid_kotlin.R
import org.lym.wanandroid_kotlin.data.db.AppDataBase
import org.lym.wanandroid_kotlin.data.repository.SearchRepository
import org.lym.wanandroid_kotlin.mvvm.ViewModelFactory
import org.lym.wanandroid_kotlin.mvvm.adapter.SearchHistoryAdapter
import org.lym.wanandroid_kotlin.mvvm.ui.BaseFragment
import org.lym.wanandroid_kotlin.mvvm.viewmodel.HistoryViewModel

/**
 * 搜索历史fragment
 *
 * author: liyaoming
 * email: liyaoming@bixin.cn
 * date: 2020-01-04-17:11
 */
class HistoryFragment : BaseFragment() {
    private val viewModel: HistoryViewModel by viewModels {
        ViewModelFactory(SearchRepository.getInstance(AppDataBase.getInstance().keyWordsDao()))
    }

    private val adapter = SearchHistoryAdapter()

    override fun getLayoutResource() = R.layout.fragment_search_history

    override fun subscribeUI() {
        viewModel.hotWords.observe(this, Observer {

        })
    }

    override fun initView() {
        rv_history.layoutManager = LinearLayoutManager(requireActivity())
        rv_history.setHasFixedSize(true)

        rv_history.adapter = adapter
        adapter.loadMoreModule?.isEnableLoadMoreIfNotFullPage = false


    }
}