package com.jascal.teresa.mvp.presenter

import android.app.Activity
import com.jascal.teresa.MyApplication
import com.jascal.teresa.base.BasePresenter
import com.jascal.teresa.mvp.contract.VideoDetailContract
import com.jascal.teresa.mvp.model.VideoDetailModel
import com.jascal.teresa.mvp.model.bean.DiscoverBean
import com.jascal.teresa.net.exception.ExceptionHandler
import com.jascal.teresa.utils.NetworkUtil
import com.jascal.teresa.utils.dataFormat
import com.jascal.teresa.utils.showToast

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
        val netType = NetworkUtil.isWifi(MyApplication.context)
        // 检测是否绑定 View
//        checkViewAttached()
        if (playInfo!!.size > 1) {
            // 当前网络是 Wifi环境下选择高清的视频
            if (netType) {
                for (i in playInfo) {
                    if (i.type == "high") {
                        val playUrl = i.url
                        mRootView?.setVideo(playUrl)
                        break
                    }
                }
            } else {
                //否则就选标清的视频
                for (i in playInfo) {
                    if (i.type == "normal") {
                        val playUrl = i.url
                        mRootView?.setVideo(playUrl)
                        //Todo 待完善
                        (mRootView as Activity).showToast("本次消耗${(mRootView as Activity)
                                .dataFormat(i.urlList[0].size)}流量")
                        break
                    }
                }
            }
        } else {
            mRootView?.setVideo(itemInfo.data.playUrl)
        }
        //设置背景
//        val backgroundUrl = itemInfo.data.cover.blurred + "/thumbnail/${DisplayManager.getScreenHeight()!! - DisplayManager.dip2px(250f)!!}x${DisplayManager.getScreenWidth()}"
//        backgroundUrl.let { mRootView?.setBackground(it) }
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