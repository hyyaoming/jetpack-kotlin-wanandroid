package org.lym.wanandroid_kotlin.mvvm.ui.gank

import android.view.View
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.chad.library.adapter.base.BaseQuickAdapter
import kotlinx.android.synthetic.main.activity_gank_meizhi.*
import org.lym.wanandroid_kotlin.R
import org.lym.wanandroid_kotlin.data.repository.GankRepository
import org.lym.wanandroid_kotlin.mvvm.ViewModelFactory
import org.lym.wanandroid_kotlin.mvvm.adapter.GankAdapter
import org.lym.wanandroid_kotlin.mvvm.ui.BaseActivity
import org.lym.wanandroid_kotlin.mvvm.viewmodel.AutoDisposeViewModel
import org.lym.wanandroid_kotlin.mvvm.viewmodel.GankViewModel
import org.lym.wanandroid_kotlin.utils.toast

/**
 * Gank.io妹纸
 *
 * author: liyaoming
 * email: liyaoming@bixin.cn
 * date: 2020-02-18-17:34
 */
class GankMeiZhiActivity : BaseActivity() {
    override fun getLayoutId() = R.layout.activity_gank_meizhi

    private val gankAdapter: GankAdapter by lazy {
        GankAdapter()
    }

    private val gankViewModel: GankViewModel by viewModels {
        ViewModelFactory(GankRepository.getInstance())
    }

    override fun getAdapter(): BaseQuickAdapter<*, *>? {
        return gankAdapter
    }

    override fun getViewModel(): AutoDisposeViewModel? {
        return gankViewModel
    }

    override fun initView() {
        rv_gank.layoutManager = GridLayoutManager(this, 2)
        rv_gank.setHasFixedSize(true)
        rv_gank.adapter = gankAdapter
    }

    override fun loadMore() {
        gankViewModel.loadGankMeiZhi(false)
    }

    override fun onItemClickListener(adapter: BaseQuickAdapter<*, *>, view: View, position: Int) {
        toast(gankAdapter.getItem(position)?.url)
    }

    override fun subscribeUI() {
        gankViewModel.meiZhi.observe(this, Observer {
            gankAdapter.setNewData(it)
        })

        gankViewModel.moreMeiZhi.observe(this, Observer {
            gankAdapter.addData(it)
        })
    }
}