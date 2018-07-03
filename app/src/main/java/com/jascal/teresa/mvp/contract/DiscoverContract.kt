package com.jascal.teresa.mvp.contract

import com.jascal.teresa.base.IPresenter
import com.jascal.teresa.base.IView
import com.jascal.teresa.mvp.model.bean.DiscoverBean

/**
 * @author jascal
 * @time 2018/6/29
 * describe
 */
interface DiscoverContract {
    interface View : IView {
        fun setDiscoverData(discoverBean: DiscoverBean)

        fun setMoreData(itemList: ArrayList<DiscoverBean.Issue.Item>)

        fun showError(msg: String, errorCode: Int)
    }

    interface Presenter : IPresenter<View> {
        fun getDiscoverData(num: Int)

        fun loadMoreData()
    }
}