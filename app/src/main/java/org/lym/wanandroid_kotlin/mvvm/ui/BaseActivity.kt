package org.lym.wanandroid_kotlin.mvvm.ui

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.qmuiteam.qmui.widget.dialog.QMUITipDialog
import org.lym.wanandroid_kotlin.common.LOAD_ERROR
import org.lym.wanandroid_kotlin.common.LOAD_FINISH
import org.lym.wanandroid_kotlin.common.LOAD_STAR
import org.lym.wanandroid_kotlin.mvvm.viewmodel.AutoDisposeViewModel
import org.lym.wanandroid_kotlin.utils.scrollTop
import org.lym.wanandroid_kotlin.weight.LoadMore

/**
 * activity 简单基类
 *
 * author: liyaoming
 * date: 2020-02-18-17:57
 */
abstract class BaseActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(getLayoutId())
        initView()
        subscribeUI()
        initConfig()
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

    private fun initConfig() {
        val adapter = getAdapter()
        val viewModel = getViewModel()
        viewModel?.apply {
            loadEnd.observe(this@BaseActivity, Observer {
                adapter?.loadMoreModule?.loadMoreEnd()
            })

            loadMoreComplete.observe(this@BaseActivity, Observer {
                adapter?.loadMoreModule?.loadMoreComplete()
            })

            requestLoading.observe(this@BaseActivity, Observer {
                when (it) {
                    LOAD_STAR ->
                        Log.d(BaseFragment.TAG, "请求开始了")
                    LOAD_ERROR ->
                        Log.d(BaseFragment.TAG, "请求出错了")
                    LOAD_FINISH ->
                        Log.d(BaseFragment.TAG, "请求结束了")
                }
            })

            requestTipLoading.observe(this@BaseActivity, Observer {
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
                QMUITipDialog.Builder(this).setIconType(QMUITipDialog.Builder.ICON_TYPE_LOADING)
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

    abstract fun subscribeUI()

    abstract fun initView()

    abstract fun getLayoutId(): Int
}