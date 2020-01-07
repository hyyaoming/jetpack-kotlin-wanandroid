package org.lym.wanandroid_kotlin.mvvm.ui.dialog

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.text.TextUtils
import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.IntDef
import androidx.annotation.LayoutRes
import androidx.core.content.ContextCompat
import org.lym.wanandroid_kotlin.R
import org.lym.wanandroid_kotlin.mvvm.ui.dialog.TipDialog.Builder.IconType
import org.lym.wanandroid_kotlin.utils.dip2px
import org.lym.wanandroid_kotlin.weight.LoadingView

/**
 * 一些基本样式的dialog
 *
 * author: liyaoming
 * email: liyaoming@bixin.cn
 * date: 2019-1-7-09:59
 */
class TipDialog @JvmOverloads constructor(context: Context, themeResId: Int = R.style.TipDialog) :
    Dialog(context, themeResId) {

    init {
        setCanceledOnTouchOutside(false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initDialogWidth()
    }

    private fun initDialogWidth() {
        val window = window
        if (window != null) {
            val wmLp = window.attributes
            wmLp.width = ViewGroup.LayoutParams.MATCH_PARENT
            window.attributes = wmLp
        }
    }

    /**
     * 生成默认的 [TipDialog]
     * 提供了一个图标和一行文字的样式, 其中图标有几种类型可选。见 [IconType]
     * @see CustomBuilder
     */
    class Builder(private val mContext: Context) {
        @IconType
        private var mCurrentIconType = ICON_TYPE_NOTHING
        private var mTipWord: CharSequence? = null

        /**
         * 限定类型
         */
        @IntDef(
            ICON_TYPE_NOTHING,
            ICON_TYPE_LOADING,
            ICON_TYPE_SUCCESS,
            ICON_TYPE_FAIL,
            ICON_TYPE_INFO
        )
        @kotlin.annotation.Retention(AnnotationRetention.SOURCE)
        annotation class IconType

        /**
         * 设置 icon 显示的内容
         *
         * @param iconType icon类型
         * @return 当前类
         */
        fun setIconType(@IconType iconType: Int): Builder {
            mCurrentIconType = iconType
            return this
        }

        /**
         * 设置显示的文案
         *
         * @param tipWord 提示文案
         * @return 当前类
         */
        fun setTipWord(tipWord: CharSequence): Builder {
            mTipWord = tipWord
            return this
        }

        /**
         * 创建 Dialog, 但没有弹出来, 如果要弹出来, 请调用返回值的 [Dialog.show] 方法
         *
         * @param cancelable 按系统返回键是否可以取消
         * @return 创建的 Dialog
         */
        fun create(cancelable: Boolean = true): TipDialog {
            val dialog = TipDialog(mContext)
            dialog.setCancelable(cancelable)
            dialog.setContentView(R.layout.tip_dialog_layout)
            val contentWrap = dialog.findViewById<ViewGroup>(R.id.contentWrap)
            if (mCurrentIconType == ICON_TYPE_LOADING) {
                val loadingView = LoadingView(mContext)
                loadingView.setColor(Color.WHITE)
                loadingView.setSize(dip2px(LOADING_SIZE))
                val loadingViewLP = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                loadingView.setLayoutParams(loadingViewLP)
                contentWrap.addView(loadingView)
            } else if (mCurrentIconType == ICON_TYPE_SUCCESS || mCurrentIconType == ICON_TYPE_FAIL || mCurrentIconType == ICON_TYPE_INFO) {
                val imageView = ImageView(mContext)
                val imageViewLP = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                imageView.layoutParams = imageViewLP
                when (mCurrentIconType) {
                    ICON_TYPE_SUCCESS -> imageView.setImageDrawable(
                        ContextCompat.getDrawable(
                            mContext,
                            R.drawable.icon_notify_done
                        )
                    )
                    ICON_TYPE_FAIL -> imageView.setImageDrawable(
                        ContextCompat.getDrawable(
                            mContext,
                            R.drawable.icon_notify_error
                        )
                    )
                    else -> imageView.setImageDrawable(
                        ContextCompat.getDrawable(
                            mContext,
                            R.drawable.icon_notify_info
                        )
                    )
                }
                contentWrap.addView(imageView)
            }
            if (mTipWord != null && mTipWord!!.length > 0) {
                val tipView = TextView(mContext)
                val tipViewLP = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                if (mCurrentIconType != ICON_TYPE_NOTHING) {
                    tipViewLP.topMargin = dip2px(TIP_WORD_TOP_MARGIN)
                }
                tipView.layoutParams = tipViewLP
                tipView.ellipsize = TextUtils.TruncateAt.END
                tipView.gravity = Gravity.CENTER
                tipView.maxLines = 2
                tipView.setTextColor(ContextCompat.getColor(mContext, android.R.color.white))
                tipView.setTextSize(TypedValue.COMPLEX_UNIT_SP, TIP_WORD_TEXT_SIZE)
                tipView.text = mTipWord
                contentWrap.minimumWidth = dip2px(DIALOG_MIN_SIZE)
                contentWrap.minimumHeight = dip2px(DIALOG_MIN_SIZE)
                contentWrap.addView(tipView)
            } else {
                contentWrap.minimumWidth = 0
                contentWrap.minimumHeight = 0
            }
            return dialog
        }

        companion object {
            /**
             * 不显示任何icon
             */
            const val ICON_TYPE_NOTHING = 0
            /**
             * 显示 Loading 图标
             */
            const val ICON_TYPE_LOADING = 1
            /**
             * 显示成功图标
             */
            const val ICON_TYPE_SUCCESS = 2
            /**
             * 显示失败图标
             */
            const val ICON_TYPE_FAIL = 3
            /**
             * 显示信息图标
             */
            const val ICON_TYPE_INFO = 4
        }
    }

    /**
     * 传入自定义的布局并使用这个布局生成 TipDialog
     */
    class CustomBuilder
    /**
     * 构造器
     *
     * @param context 上下文
     */
        (private val mContext: Context) {
        private var mContentLayoutId: Int = 0

        /**
         * 自定义布局id
         *
         * @param layoutId 布局文件id
         * @return 返回当前类
         */
        fun setContent(@LayoutRes layoutId: Int): CustomBuilder {
            mContentLayoutId = layoutId
            return this
        }

        /**
         * 创建 Dialog, 但没有弹出来, 如果要弹出来, 请调用返回值的 [Dialog.show] 方法
         *
         * @return 创建的 Dialog
         */
        fun create(): TipDialog {
            val dialog = TipDialog(mContext)
            dialog.setContentView(R.layout.tip_dialog_layout)
            val contentWrap = dialog.findViewById<ViewGroup>(R.id.contentWrap)
            LayoutInflater.from(mContext).inflate(mContentLayoutId, contentWrap, true)
            return dialog
        }
    }

    companion object {
        private const val LOADING_SIZE = 30f
        private const val TIP_WORD_TOP_MARGIN = 12f
        private const val TIP_WORD_TEXT_SIZE = 14f
        private const val DIALOG_MIN_SIZE = 90f
    }
}
