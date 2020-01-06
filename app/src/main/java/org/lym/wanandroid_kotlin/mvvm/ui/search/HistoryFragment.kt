package org.lym.wanandroid_kotlin.mvvm.ui.search

import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.fragment_search_history.*
import org.lym.wanandroid_kotlin.R
import org.lym.wanandroid_kotlin.data.db.AppDataBase
import org.lym.wanandroid_kotlin.data.db.model.KeyWord
import org.lym.wanandroid_kotlin.data.model.HotWordModel
import org.lym.wanandroid_kotlin.data.repository.SearchRepository
import org.lym.wanandroid_kotlin.mvvm.ViewModelFactory
import org.lym.wanandroid_kotlin.mvvm.adapter.SearchHistoryAdapter
import org.lym.wanandroid_kotlin.mvvm.ui.BaseFragment
import org.lym.wanandroid_kotlin.mvvm.viewmodel.HistoryViewModel
import org.lym.wanandroid_kotlin.utils.dip2px
import org.lym.wanandroid_kotlin.utils.hide
import org.lym.wanandroid_kotlin.utils.toast
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
    private lateinit var headView: View
    private lateinit var searchView: ActionBarSearch

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
            adapter.setNewData(it)
        })
    }

    private fun bindHistory(list: List<KeyWord>) {
        val historyFl = headView.findViewById<FloatLayout>(R.id.fl_history)
        val historyIcon = headView.findViewById<TextView>(R.id.tv_history_search)
        val clearHistory = headView.findViewById<ImageView>(R.id.iv_clear_history)
        if (list.isEmpty()) {
            historyFl.visibility = View.GONE
            historyIcon.visibility = View.GONE
            clearHistory.visibility = View.GONE
        } else {
            historyFl.visibility = View.VISIBLE
            historyIcon.visibility = View.VISIBLE
            clearHistory.visibility = View.VISIBLE
            clearHistory.setOnClickListener {
                viewModel.clearHistory()
            }
            historyFl.removeAllViews()
            list.forEach { word ->
                historyFl.addView(getTagView(word.wordName).apply {
                    setOnClickListener {
                        search(word.wordName)
                    }
                })
            }
        }

    }

    private fun addHeadView(list: List<HotWordModel>) {
        headView = LayoutInflater.from(requireActivity())
            .inflate(R.layout.head_hot_and_history_word_layout, rv_history, false)
        val hotFl = headView.findViewById<FloatLayout>(R.id.fl_hot_word)
        hotFl.removeAllViews()
        list.forEach { word ->
            hotFl.addView(getTagView(word.name).apply {
                setOnClickListener {
                    search(word.name)
                }
            })
        }

        viewModel.searchRepository.getHistoryWords().observe(this, Observer {
            bindHistory(it)
        })
        adapter.setHeaderView(headView)
    }

    private fun getTagView(content: CharSequence) = TextView(requireContext()).apply {
        textSize = 13f
        setPadding(dip2px(6f), dip2px(6f), dip2px(6f), dip2px(6f))
        setTextColor(ContextCompat.getColor(requireContext(), R.color.color_333333))
        text = content
        background = GradientDrawable().apply {
            setColor(ContextCompat.getColor(requireContext(), R.color.color_F1F1F1))
            cornerRadius = dip2px(8f).toFloat()
        }
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

        searchView = requireActivity().findViewById<ActionBarSearch>(R.id.search_view)
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

        searchView.setOnLeftIconClickListener {
            val controller =
                requireActivity().findNavController(R.id.nav_search_host_fragment)
            val currentDestination = controller.currentDestination
            val destId = currentDestination!!.id
            val parent = currentDestination.parent
            if (parent!!.startDestination == destId) {
                requireActivity().finish()
            } else {
                controller.navigateUp()
            }
        }

        searchView.setOnRightTextClickListener {
            searchView.editTextView.text.trim().toString().let {
                if (it.isNotEmpty()) {
                    search(it)
                } else {
                    toast(getString(R.string.please_input_search_key))
                }
            }
        }

        rv_history.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                hide(searchView.editTextView)
            }
        })
    }

    private fun search(key: String) {
        searchView.editTextView.setText(key)
        searchView.editTextView.setSelection(searchView.editTextView.length())
        viewModel.searchRepository.insertSearchWord(key)
        requireActivity().findNavController(R.id.nav_search_host_fragment)
            .navigate(
                R.id.action_search_result_fragment,
                Bundle().apply { putString("search_key", key) })
    }

}