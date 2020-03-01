package org.lym.wanandroid_kotlin.mvvm.ui.gank

import android.view.View
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import com.chad.library.adapter.base.BaseQuickAdapter
import kotlinx.android.synthetic.main.activity_gank_meizhi.*
import org.lym.wanandroid_kotlin.R
import org.lym.wanandroid_kotlin.data.repository.GankRepository
import org.lym.wanandroid_kotlin.mvvm.ViewModelFactory
import org.lym.wanandroid_kotlin.mvvm.adapter.GankAdapter
import org.lym.wanandroid_kotlin.mvvm.ui.BaseActivity
import org.lym.wanandroid_kotlin.mvvm.viewmodel.AutoDisposeViewModel
import org.lym.wanandroid_kotlin.mvvm.viewmodel.GankViewModel
import org.lym.wanandroid_kotlin.weight.preview.loader.GlideImageLoader
import org.lym.wanandroid_kotlin.weight.preview.style.index.NumberIndexIndicator
import org.lym.wanandroid_kotlin.weight.preview.style.progress.ProgressBarIndicator
import org.lym.wanandroid_kotlin.weight.preview.transfer.TransferConfig
import org.lym.wanandroid_kotlin.weight.preview.transfer.Transferee

/**
 * Gank.io妹纸
 *
 * author: liyaoming
 * email: liyaoming@bixin.cn
 * date: 2020-02-18-17:34
 */
class GankMeiZhiActivity : BaseActivity() {
    private lateinit var transferee: Transferee
    private lateinit var config: TransferConfig

    private val gankViewModel: GankViewModel by viewModels {
        ViewModelFactory(GankRepository.getInstance())
    }

    override fun getAdapter(): BaseQuickAdapter<*, *>? {
        return gankAdapter
    }

    private val gankAdapter: GankAdapter by lazy {
        GankAdapter()
    }

    override fun getViewModel(): AutoDisposeViewModel? {
        return gankViewModel
    }

    override fun initView() {
        rv_gank.layoutManager = GridLayoutManager(this, 2)
        rv_gank.setHasFixedSize(true)
        rv_gank.adapter = gankAdapter
        initPreviewConfig()
    }

    override fun getLayoutId() = R.layout.activity_gank_meizhi

    private fun initPreviewConfig() {
        transferee = Transferee.getDefault(this)
        config = TransferConfig.build()
            .setProgressIndicator(ProgressBarIndicator())
            .setIndexIndicator(NumberIndexIndicator())
            .setImageLoader(GlideImageLoader.with(this))
            .setJustLoadHitImage(true)
            .bindRecyclerView(rv_gank, R.id.iv_gank)
    }

    private fun getPreviewUrls(): MutableList<String> {
        return mutableListOf<String>().apply {
            gankAdapter.data.forEach {
                this.add(it.httpsUrl())
            }
        }
    }

    override fun loadMore() {
        gankViewModel.loadGankMeiZhi(false)
    }

    override fun onItemClickListener(adapter: BaseQuickAdapter<*, *>, view: View, position: Int) {
        config.nowThumbnailIndex = position
        config.sourceImageList = getPreviewUrls()
        transferee.apply(config).show()
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