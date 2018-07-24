package com.jascal.teresa.mvp.presenter

import com.jascal.teresa.base.BasePresenter
import com.jascal.teresa.mvp.contract.VideoDetailContract
import com.jascal.teresa.mvp.model.VideoDetailModel
import com.jascal.teresa.mvp.model.bean.DiscoverBean
import com.jascal.teresa.net.exception.ExceptionHandler

/**
 * @author jascal
 * @time 2018/7/3
 * describe
 */
class VideoDetailPresenter : BasePresenter<VideoDetailContract.View>(), VideoDetailContract.Presenter {
    private val videoDetailModel: VideoDetailModel by lazy {
        VideoDetailModel()
    }

    override fun loadVideoInfo(itemInfo: DiscoverBean.Issue.Item) {
        val playInfo = itemInfo.data?.playInfo
        if (playInfo!!.size > 1) {
            for (i in playInfo) {
                val playUrl = i.url
                mRootView?.setVideo(playUrl)
                break
            }
        } else {
            mRootView?.setVideo(itemInfo.data.playUrl)
        }
        mRootView?.setVideoInfo(itemInfo)
    }

    override fun requestRelatedVideo(id: Long) {
        mRootView?.showLoading()
        val disposable = videoDetailModel.requestRelatedData(id)
                .subscribe({ issue ->
                    mRootView?.apply {
                        dismissLoading()
                        setRecentRelatedVideo(issue.itemList)
                    }
                }, { t ->
                    mRootView?.apply {
                        dismissLoading()
                        setErrorMsg(ExceptionHandler.handleException(t))
                    }
                })
        addSubscription(disposable)
    }
}