package com.jascal.teresa.ui.fragment

import android.os.Bundle
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import com.classic.common.MultipleStatusView
import com.jascal.teresa.R
import com.jascal.teresa.base.BaseFragment
import com.jascal.teresa.mvp.contract.DiscoverContract
import com.jascal.teresa.mvp.model.bean.DiscoverBean
import com.jascal.teresa.mvp.presenter.DiscoverPresenter
import com.jascal.teresa.net.exception.ErrorStatus
import com.jascal.teresa.ui.adapter.DiscoverAdapter
import com.jascal.teresa.utils.StatusBarUtil
import com.jascal.teresa.utils.showToast
import com.orhanobut.logger.Logger
import com.scwang.smartrefresh.header.MaterialHeader
import kotlinx.android.synthetic.main.fragment_discover.*
import java.text.SimpleDateFormat
import java.util.*

/**
 * @author jascal
 * @time 2018/6/29
 * describe
 * fresco
 * 插件化+组件化+热修复
 * ndk 底层
 * 流畅性
 * jni rpc
 * delay
 * jsoup爬虫框架
 */
class DiscoverFragment : BaseFragment(), DiscoverContract.View {
    private val mPresenter by lazy { DiscoverPresenter() }
    private var mTitle: String? = null
    private var num: Int = 1
    private var mDiscoverAdapter: DiscoverAdapter? = null
    private var loadingMore = false
    private var isRefresh = false
    private var mMaterialHeader: MaterialHeader? = null

    protected var mLayoutStatusView: MultipleStatusView? = null

    companion object {
        fun getInstance(title: String): DiscoverFragment {
            val fragment = DiscoverFragment()
            val bundle = Bundle()
            fragment.arguments = bundle
            fragment.mTitle = title
            return fragment
        }
    }

    override fun initView() {
        Log.d("discoverFragment", "initView")
        mPresenter.attachView(this)
        mRefreshLayout.setEnableHeaderTranslationContent(true)
        mRefreshLayout.setOnRefreshListener {
            isRefresh = true
            mPresenter.getDiscoverData(num)
        }
        mMaterialHeader = mRefreshLayout.refreshHeader as MaterialHeader?
        //打开下拉刷新区域块背景:
        mMaterialHeader?.setShowBezierWave(true)
        //设置下拉刷新主题颜色
        mRefreshLayout.setPrimaryColorsId(R.color.titleColorItem, R.color.titleColorBg)
        mRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView?, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    val childCount = mRecyclerView.childCount
                    val itemCount = mRecyclerView.layoutManager.itemCount
                    val firstVisibleItem = (mRecyclerView.layoutManager as LinearLayoutManager).findFirstVisibleItemPosition()
                    if (firstVisibleItem + childCount == itemCount) {
                        if (!loadingMore) {
                            loadingMore = true
                            mPresenter.loadMoreData()
                        }
                    }
                }
            }

            //RecyclerView滚动的时候调用
            override fun onScrolled(recyclerView: RecyclerView?, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val currentVisibleItemPosition = linearLayoutManager.findFirstVisibleItemPosition()
                toolbar.setBackgroundColor(getColor(R.color.titleColorBg))
                iv_search.setImageResource(R.mipmap.ic_action_search_white)
                tv_header_title.text = "search"
                if (currentVisibleItemPosition == 0) {
                    //背景设置为透明
//                    toolbar.setBackgroundColor(getColor(R.color.titleColorItem))
//                    iv_search.setImageResource(R.mipmap.ic_action_search_white)
//                    tv_header_title.text = "search"
                } else {
                    if (mDiscoverAdapter?.mData!!.size > 1) {
//                        toolbar.setBackgroundColor(getColor(R.color.titleColorBg))
//                        iv_search.setImageResource(R.mipmap.ic_action_search_black)
                        val itemList = mDiscoverAdapter!!.mData
                        val item = itemList[currentVisibleItemPosition + mDiscoverAdapter!!.bannerItemSize - 1]
                        if (item.type == "textHeader") {
                            tv_header_title.text = item.data?.text
                        } else {
                            tv_header_title.text = simpleDateFormat.format(item.data?.date)
                        }
                    }
                }
            }
        })
//        iv_search.setOnClickListener { openSearchActivity() }
        mLayoutStatusView = multipleStatusView
        lazyLoad()
        //状态栏透明和间距处理
        StatusBarUtil.darkMode(activity)
        StatusBarUtil.setPaddingSmart(activity, toolbar)
    }

    override fun getLayoutId(): Int = R.layout.fragment_discover

    init {

    }

    override fun lazyLoad() {
        mPresenter.getDiscoverData(num)
    }


    private val linearLayoutManager by lazy {
        LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
    }

    override fun setDiscoverData(discoverBean: DiscoverBean) {
        Log.d("discoverFragment", "setDiscoverData")
        mLayoutStatusView?.showContent()
        Logger.d(discoverBean)

        // Adapter
        mDiscoverAdapter = DiscoverAdapter(activity, discoverBean.issueList[0].itemList)
        //设置 banner 大小
        mDiscoverAdapter?.setBannerSize(discoverBean.issueList[0].count)

        mRecyclerView.adapter = mDiscoverAdapter
        mRecyclerView.layoutManager = linearLayoutManager
        mRecyclerView.itemAnimator = DefaultItemAnimator()
    }

    override fun setMoreData(itemList: ArrayList<DiscoverBean.Issue.Item>) {
        loadingMore = false
        mDiscoverAdapter?.addItemData(itemList)
    }

    override fun showError(msg: String, errorCode: Int) {
        Log.d("discoverFragment", "show Error:$msg:$errorCode")
        showToast(msg)
        if (errorCode == ErrorStatus.NETWORK_ERROR) {
            mLayoutStatusView?.showNoNetwork()
        } else {

            mLayoutStatusView?.showError()
        }
    }

    override fun showLoading() {
        if (!isRefresh) {
            isRefresh = false
            mLayoutStatusView?.showLoading()
        }
    }

    override fun dismissLoading() {
        mRefreshLayout.finishRefresh()
    }

    fun getColor(colorId: Int): Int {
        return resources.getColor(colorId)
    }

    private val simpleDateFormat by lazy {
        SimpleDateFormat("- MMM. dd, 'Brunch' -", Locale.ENGLISH)
    }
}