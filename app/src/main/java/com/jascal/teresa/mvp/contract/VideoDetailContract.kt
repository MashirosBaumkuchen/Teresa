package com.jascal.teresa.mvp.contract

import com.jascal.teresa.base.IPresenter
import com.jascal.teresa.base.IView
import com.jascal.teresa.mvp.model.bean.DiscoverBean

/**
 * @author jascal
 * @time 2018/7/3
 * describe
 */
interface VideoDetailContract {
    interface View : IView {
        fun setVideo(url: String)

        fun setVideoInfo(itemInfo: DiscoverBean.Issue.Item)

        fun setRecentRelatedVideo(itemList: ArrayList<DiscoverBean.Issue.Item>)

        fun setErrorMsg(errorMsg: String)
    }

    interface Presenter : IPresenter<View> {
        fun loadVideoInfo(itemInfo: DiscoverBean.Issue.Item)

        fun requestRelatedVideo(id: Long)
    }
}