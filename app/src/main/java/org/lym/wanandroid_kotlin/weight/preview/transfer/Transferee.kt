package org.lym.wanandroid_kotlin.weight.preview.transfer

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.content.DialogInterface.OnShowListener
import android.view.KeyEvent
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import org.lym.wanandroid_kotlin.weight.preview.transfer.TransferLayout.OnLayoutResetListener
import java.io.File
import java.util.*

/**
 * Main workflow: <br></br>
 * 1、点击缩略图展示缩略图到 transferee 过渡动画 <br></br>
 * 2、显示下载高清图片进度 <br></br>
 * 3、加载完成显示高清图片 <br></br>
 * 4、高清图支持手势缩放 <br></br>
 * 5、关闭 transferee 展示 transferee 到原缩略图的过渡动画 <br></br>
 * Created by hitomi on 2017/1/19.
 *
 *
 * email: 196425254@qq.com
 */
class Transferee private constructor(private val context: Context) : OnShowListener,
    DialogInterface.OnKeyListener, OnLayoutResetListener {
    private lateinit var transDialog: Dialog
    private lateinit var transLayout: TransferLayout
    private lateinit var transConfig: TransferConfig
    private var transListener: OnTransfereeStateChangeListener? = null
    /**
     * transferee 是否显示
     *
     * @return true ：显示, false ：关闭
     */
    // 因为Dialog的关闭有动画延迟，固不能使用 dialog.isShowing, 去判断 transferee 的显示逻辑
    var isShown = false
        private set

    private fun createLayout() {
        transLayout = TransferLayout(context)
        transLayout.setOnLayoutResetListener(this)
    }

    private fun createDialog() {
        transDialog = AlertDialog.Builder(context, dialogStyle)
            .setView(transLayout)
            .create()
        transDialog.setOnShowListener(this)
        transDialog.setOnKeyListener(this)
    }

    /**
     * 兼容4.4以下的全屏 Dialog 样式
     *
     * @return The style of the dialog
     */
    private val dialogStyle: Int
        get() = android.R.style.Theme_Translucent_NoTitleBar_Fullscreen

    /**
     * 检查参数，如果必须参数缺少，就使用缺省参数或者抛出异常
     */
    private fun checkConfig() {
        require(!transConfig.isSourceEmpty) { "The parameter sourceImageList can't be empty" }
        transConfig.nowThumbnailIndex = transConfig.nowThumbnailIndex.coerceAtLeast(0)
        transConfig.offscreenPageLimit =
            if (transConfig.offscreenPageLimit <= 0) 1 else transConfig.offscreenPageLimit
        transConfig.duration = if (transConfig.duration <= 0) 300 else transConfig.duration
    }

    private fun fillOriginImages() {
        val originImageList: MutableList<ImageView?> = ArrayList()
        when {
            transConfig.recyclerView != null -> {
                fillByRecyclerView(originImageList)
            }
            transConfig.listView != null -> {
                fillByListView(originImageList)
            }
            transConfig.imageView != null -> {
                originImageList.add(transConfig.imageView)
            }
        }
        transConfig.originImageList = originImageList
    }

    private fun fillByRecyclerView(originImageList: MutableList<ImageView?>) {
        transConfig.recyclerView?.let {
            val childCount = it.childCount
            for (i in 0 until childCount) {
                val originImage = it.getChildAt(i)
                    .findViewById<View>(transConfig.imageId) as ImageView
                originImageList.add(originImage)
            }
            val layoutManager = it.layoutManager ?: return
            var firstPos = 0
            var lastPos = 0
            val totalCount = layoutManager.itemCount
            if (layoutManager is GridLayoutManager) {
                firstPos = layoutManager.findFirstVisibleItemPosition()
                lastPos = layoutManager.findLastVisibleItemPosition()
            } else if (layoutManager is LinearLayoutManager) {
                firstPos = layoutManager.findFirstVisibleItemPosition()
                lastPos = layoutManager.findLastVisibleItemPosition()
            }
            fillPlaceHolder(originImageList, totalCount, firstPos, lastPos)
        }
    }

    private fun fillByListView(originImageList: MutableList<ImageView?>) {
        transConfig.listView?.let {
            val childCount = it.childCount
            for (i in 0 until childCount) {
                val originImage = it.getChildAt(i)
                    .findViewById<View>(transConfig.imageId) as ImageView
                originImageList.add(originImage)
            }
            val firstPos = it.firstVisiblePosition
            val lastPos = it.lastVisiblePosition
            val totalCount = it.count
            fillPlaceHolder(originImageList, totalCount, firstPos, lastPos)
        }
    }

    private fun fillPlaceHolder(
        originImageList: MutableList<ImageView?>,
        totalCount: Int,
        firstPos: Int,
        lastPos: Int
    ) {
        if (firstPos > 0) {
            for (pos in firstPos downTo 1) {
                originImageList.add(0, null)
            }
        }
        if (lastPos < totalCount) {
            for (i in totalCount - 1 - lastPos downTo 1) {
                originImageList.add(null)
            }
        }
    }

    /**
     * 配置 transferee 参数对象
     *
     * @param config 参数对象
     * @return transferee
     */
    fun apply(config: TransferConfig): Transferee {
        if (!isShown) {
            transConfig = config
            fillOriginImages()
            checkConfig()
            transLayout.apply(config)
        }
        return this
    }

    /**
     * 显示 transferee
     */
    fun show() {
        if (isShown) {
            return
        }
        transDialog.show()
        transListener?.onShow()
        isShown = true
    }

    /**
     * 显示 transferee, 并设置 OnTransfereeChangeListener
     *
     * @param listener [OnTransfereeStateChangeListener]
     */
    fun show(listener: OnTransfereeStateChangeListener?) {
        if (isShown) {
            return
        }
        transDialog.show()
        transListener = listener
        transListener?.onShow()
        isShown = true
    }

    /**
     * 关闭 transferee
     */
    private fun dismiss() {
        if (!isShown) return
        transLayout.dismiss(transConfig.nowThumbnailIndex)
        isShown = false
    }

    /**
     * 获取图片文件
     */
    fun getImageFile(imageUri: String?): File? {
        return transConfig.imageLoader.getCache(imageUri)
    }

    /**
     * 清除 transferee 缓存
     */
    fun clear() {
        transConfig.imageLoader.clearCache()
    }

    override fun onShow(dialog: DialogInterface) {
        transLayout.show()
    }

    override fun onReset() {
        transDialog.dismiss()
        transListener?.onDismiss()
        isShown = false
    }

    override fun onKey(
        dialog: DialogInterface,
        keyCode: Int,
        event: KeyEvent
    ): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.action == KeyEvent.ACTION_UP &&
            !event.isCanceled
        ) {
            dismiss()
        }
        return true
    }

    /**
     * 设置 Transferee 显示和关闭的监听器
     *
     * @param listener [OnTransfereeStateChangeListener]
     */
    fun setOnTransfereeStateChangeListener(listener: OnTransfereeStateChangeListener?) {
        transListener = listener
    }

    /**
     * Transferee 显示的时候调用 [OnTransfereeStateChangeListener.onShow]
     *
     *
     * Transferee 关闭的时候调用 [OnTransfereeStateChangeListener.onDismiss]
     */
    interface OnTransfereeStateChangeListener {
        fun onShow()
        fun onDismiss()
    }

    interface OnTransfereeLongClickListener {
        fun onLongClick(
            imageView: ImageView?,
            imageUri: String?,
            pos: Int
        )
    }

    companion object {
        /**
         * @param context   Context
         * @return [Transferee]
         */
        fun getDefault(context: Context): Transferee {
            return Transferee(context)
        }
    }

    /**
     * 构造方法私有化，通过[.getDefault] 创建 transferee
     *
     * @param context 上下文环境
     */
    init {
        createLayout()
        createDialog()
    }
}