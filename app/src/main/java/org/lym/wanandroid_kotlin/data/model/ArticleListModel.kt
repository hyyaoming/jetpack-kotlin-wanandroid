package org.lym.wanandroid_kotlin.data.model

import com.chad.library.adapter.base.entity.MultiItemEntity

/**
 * 首页文章列表model
 *
 * author: liyaoming
 * date: 2020-01-02-15:05
 */
data class ArticleListModel(
    var curPage: Int,
    var offset: Int,
    var over: Boolean,
    var pageCount: Int,
    var size: Int,
    var total: Int, var datas: MutableList<ArticleModel>
)

data class ArticleModel(
    var apkLink: String,
    var author: String,
    var shareUser: String,
    var chapterId: Int,
    var chapterName: String,
    var collect: Boolean,
    var courseId: Int,
    var desc: String,
    var envelopePic: String,
    var fresh: Boolean,
    var id: Int,
    var link: String,
    var niceDate: String,
    var origin: String,
    var prefix: String,
    var projectLink: String,
    var publishTime: Long,
    var superChapterId: Int,
    var superChapterName: String,
    var title: String,
    var type: Int,
    var userId: Int,
    var visible: Int,
    var zan: Int,
    var originId: Int
) : MultiItemEntity {
    override val itemType: Int
        get() = ARTICLE

    fun getUser(): String {
        if (author.isNotEmpty()) {
            return author
        } else if (shareUser.isNotEmpty()) {
            return shareUser
        }
        return "匿名"
    }
}