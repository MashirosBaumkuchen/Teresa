package com.jascal.teresa.mvp.presenter

import android.util.Log
import com.jascal.teresa.base.BasePresenter
import com.jascal.teresa.mvp.contract.DiscoverContract
import com.jascal.teresa.mvp.model.DiscoverModel
import com.jascal.teresa.mvp.model.bean.DiscoverBean
import com.jascal.teresa.net.exception.ExceptionHandler

/**
 * @author jascal
 * @time 2018/6/29
 * describe
 */
class DiscoverPresenter : BasePresenter<DiscoverContract.View>(), DiscoverContract.Presenter {
    private var bannerHomeBean: DiscoverBean? = null
    private var nextPageUrl: String? = null     //加载首页的Banner 数据+一页数据合并后，nextPageUrl没 add
    private val homeModel: DiscoverModel by lazy {
        DiscoverModel()
    }

    override fun getDiscoverData(num: Int) {
        // 检测是否绑定 View
        checkViewAttache()
        mRootView?.showLoading()
        val disposable = homeModel.requestDiscoverData(num)
                .flatMap { homeBean ->
                    //过滤掉 Banner2(包含广告,等不需要的 Type), 具体查看接口分析
                    val bannerItemList = homeBean.issueList[0].itemList
                    bannerItemList.filter { item ->
                        item.type == "banner2" || item.type == "horizontalScrollCard"
                    }.forEach { item ->
                        //移除 item
                        bannerItemList.remove(item)
                    }
                    bannerHomeBean = homeBean //记录第一页是当做 banner 数据
                    //根据 nextPageUrl 请求下一页数据
                    homeModel.loadMoreData(homeBean.nextPageUrl)
                }
                .subscribe({ homeBean ->
                    mRootView?.apply {
                        Log.d("discoverPresenter", "bannerHomeBean")
                        dismissLoading()
                        nextPageUrl = homeBean.nextPageUrl
                        //过滤掉 Banner2(包含广告,等不需要的 Type), 具体查看接口分析
                        val newBannerItemList = homeBean.issueList[0].itemList
                        newBannerItemList.filter { item ->
                            item.type == "banner2" || item.type == "horizontalScrollCard"
                        }.forEach { item ->
                            //移除 item
                            newBannerItemList.remove(item)
                        }
                        // 重新赋值 Banner 长度
                        bannerHomeBean!!.issueList[0].count = bannerHomeBean!!.issueList[0].itemList.size
                        //赋值过滤后的数据 + banner 数据
                        bannerHomeBean?.issueList!![0].itemList.addAll(newBannerItemList)
                        setDiscoverData(bannerHomeBean!!)
                    }
                }, { t ->
                    mRootView?.apply {
                        dismissLoading()
                        showError(ExceptionHandler.handleException(t), ExceptionHandler.errorCode)
                    }
                })
        addSubscription(disposable)
    }

    override fun loadMoreData() {
        val disposable = nextPageUrl?.let {
            homeModel.loadMoreData(it)
                    .subscribe({ homeBean ->
                        mRootView?.apply {
                            //过滤掉 Banner2(包含广告,等不需要的 Type), 具体查看接口分析
                            val newItemList = homeBean.issueList[0].itemList
                            newItemList.filter { item ->
                                item.type == "banner2" || item.type == "horizontalScrollCard"
                            }.forEach { item ->
                                //移除 item
                                newItemList.remove(item)
                            }
                            nextPageUrl = homeBean.nextPageUrl
                            setMoreData(newItemList)
                        }
                    }, { t ->
                        mRootView?.apply {
                            showError(ExceptionHandler.handleException(t), ExceptionHandler.errorCode)
                        }
                    })
        }
        if (disposable != null) {
            addSubscription(disposable)
        }
    }

}