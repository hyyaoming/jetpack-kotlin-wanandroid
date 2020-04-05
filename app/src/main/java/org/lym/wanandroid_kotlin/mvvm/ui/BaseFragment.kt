package org.lym.wanandroid_kotlin.mvvm.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.qmuiteam.qmui.widget.dialog.QMUITipDialog
import org.lym.wanandroid_kotlin.common.LOAD_ERROR
import org.lym.wanandroid_kotlin.common.LOAD_FINISH
import org.lym.wanandroid_kotlin.common.LOAD_STAR
import org.lym.wanandroid_kotlin.mvvm.viewmodel.AutoDisposeViewModel
import org.lym.wanandroid_kotlin.utils.scrollTop
import org.lym.wanandroid_kotlin.utils.view
import org.lym.wanandroid_kotlin.weight.LoadMore

/**
 * fragment基类
 *
 * author: liyaoming
 * date: 2020-01-02-15:24
 */
abstract class BaseFragment : Fragment() {
    abstract fun getLayoutResource(): Int

    abstract fun subscribeUI()

    abstract fun initView()

    companion object {
        const val TAG = "BaseFragment"
    }

    protected open fun getAdapter(): BaseQuickAdapter<*, *>? {
        return null
    }

    open fun getViewModel(): AutoDisposeViewModel? {
        return null
    }

    fun scrollToTop() {
        val recyclerView = getRecyclerView()
        recyclerView?.scrollTop(24)
    }

    protected open fun getRecyclerView(): RecyclerView? {
        return null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        subscribeUI()
        initConfig()
    }

    private fun initConfig() {
        val adapter = getAdapter()
        val viewModel = getViewModel()
        viewModel?.apply {
            loadEnd.observe(this@BaseFragment, Observer {
                adapter?.loadMoreModule?.loadMoreEnd()
            })

            loadMoreComplete.observe(this@BaseFragment, Observer {
                adapter?.loadMoreModule?.loadMoreComplete()
            })

            requestLoading.observe(this@BaseFragment, Observer {
                when (it) {
                    LOAD_STAR ->
                        Log.d(TAG, "请求开始了")
                    LOAD_ERROR ->
                        Log.d(TAG, "请求出错了")
                    LOAD_FINISH ->
                        Log.d(TAG, "请求结束了")
                }
            })

            requestTipLoading.observe(this@BaseFragment, Observer {
                when (it) {
                    LOAD_STAR ->
                        getTipDialog()?.show()
                    else -> {
                        getTipDialog()?.dismiss()
                    }
                }
            })
        }

        adapter?.apply {
            loadMoreModule?.loadMoreView = LoadMore.loadMoreView
            loadMoreModule?.preLoadNumber = 0
            loadMoreModule?.setOnLoadMoreListener {
                loadMore()
            }

            setOnItemClickListener { adapter, view, position ->
                onItemClickListener(adapter, view, position)
            }

            setOnItemChildClickListener { adapter, view, position ->
                onItemChildClickListener(adapter, view, position)
            }
        }
    }

    private var tipLoading: QMUITipDialog? = null

    private fun getTipDialog(): QMUITipDialog? {
        if (tipLoading == null) {
            tipLoading =
                QMUITipDialog.Builder(requireContext())
                    .setIconType(QMUITipDialog.Builder.ICON_TYPE_LOADING)
                    .create(true);
        }
        return tipLoading
    }

    protected open fun loadMore() {

    }

    protected open fun onItemClickListener(
        adapter: BaseQuickAdapter<*, *>,
        view: View,
        position: Int
    ) {

    }

    protected open fun onItemChildClickListener(
        adapter: BaseQuickAdapter<*, *>,
        view: View,
        position: Int
    ) {

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return activity?.view(getLayoutResource(), container, false)
    }
}