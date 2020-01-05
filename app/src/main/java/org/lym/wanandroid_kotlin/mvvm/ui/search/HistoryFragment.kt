package org.lym.wanandroid_kotlin.mvvm.ui.search

import android.graphics.drawable.GradientDrawable
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_search_history.*
import org.lym.wanandroid_kotlin.R
import org.lym.wanandroid_kotlin.data.db.AppDataBase
import org.lym.wanandroid_kotlin.data.model.HotWordModel
import org.lym.wanandroid_kotlin.data.repository.SearchRepository
import org.lym.wanandroid_kotlin.mvvm.ViewModelFactory
import org.lym.wanandroid_kotlin.mvvm.adapter.SearchHistoryAdapter
import org.lym.wanandroid_kotlin.mvvm.ui.BaseFragment
import org.lym.wanandroid_kotlin.mvvm.viewmodel.HistoryViewModel
import org.lym.wanandroid_kotlin.utils.dip2px
import org.lym.wanandroid_kotlin.weight.FloatLayout
import per.goweii.actionbarex.common.ActionBarSearch

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

    private val adapter: SearchHistoryAdapter by lazy {
        SearchHistoryAdapter()
    }

    override fun getLayoutResource() = R.layout.fragment_search_history

    override fun subscribeUI() {
        viewModel.hotWords.observe(this, Observer {
            addHeadView(it)
        })

        viewModel.topArticle.observe(this, Observer {
            adapter.addData(it)
        })

        viewModel.searchRepository.getHistoryWords().observe(this, Observer {
        })
    }

    private fun addHeadView(it: List<HotWordModel>) {
        val hotWord = LayoutInflater.from(requireActivity())
            .inflate(R.layout.head_hot_word_layout, rv_history, false)
        val hotFl = hotWord.findViewById<FloatLayout>(R.id.fl_hot_word)
        it.forEach {
            TextView(requireContext()).apply {
                textSize = 13f
                setPadding(dip2px(6f), dip2px(6f), dip2px(6f), dip2px(6f))
                setTextColor(ContextCompat.getColor(requireContext(), R.color.color_333333))
                text = it.name
                background = GradientDrawable().apply {
                    setColor(ContextCompat.getColor(requireContext(), R.color.color_F5FFFA))
                    cornerRadius = dip2px(8f).toFloat()
                }
                hotFl.addView(this)
            }
        }
        adapter.addHeaderView(hotWord)
    }

    override fun initView() {
        rv_history.layoutManager = activity?.let {
            LinearLayoutManager(it).apply {
                orientation = LinearLayoutManager.VERTICAL
            }
        }
        rv_history.setHasFixedSize(true)

        rv_history.adapter = adapter
        adapter.loadMoreModule?.isEnableLoadMoreIfNotFullPage = false

        val searchView = requireActivity().findViewById<ActionBarSearch>(R.id.search_view)
        searchView.editTextView.imeOptions = EditorInfo.IME_ACTION_SEARCH
        searchView.editTextView.setOnEditorActionListener(object :
            TextView.OnEditorActionListener {
            override fun onEditorAction(v: TextView?, actionId: Int, event: KeyEvent?): Boolean {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    val key = searchView.editTextView.text.toString()
                    search(key)
                    return true
                }
                return false
            }
        })
    }

    private fun search(key: String) {
        viewModel.searchRepository.insertSearchWord(key)
        requireActivity().findNavController(R.id.nav_search_host_fragment)
            .navigate(R.id.action_search_result_fragment)
    }

}