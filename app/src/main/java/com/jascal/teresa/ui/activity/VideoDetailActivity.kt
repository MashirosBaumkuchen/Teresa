package com.jascal.teresa.ui.activity

import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.content.res.Configuration
import android.os.Build
import android.support.v4.view.ViewCompat
import android.support.v7.widget.LinearLayoutManager
import android.transition.Transition
import android.widget.ImageView
import com.bumptech.glide.load.DecodeFormat
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.jascal.teresa.MyApplication
import com.jascal.teresa.R
import com.jascal.teresa.base.BaseActivity
import com.jascal.teresa.glide.GlideApp
import com.jascal.teresa.mvp.contract.VideoDetailContract
import com.jascal.teresa.mvp.model.bean.DiscoverBean
import com.jascal.teresa.mvp.presenter.VideoDetailPresenter
import com.jascal.teresa.ui.adapter.VideoDetailAdapter
import com.jascal.teresa.utils.CleanLeakUtils
import com.jascal.teresa.utils.Constants
import com.jascal.teresa.utils.StatusBarUtil
import com.jascal.teresa.utils.WatchHistoryUtils
import com.orhanobut.logger.Logger
import com.scwang.smartrefresh.header.MaterialHeader
import kotlinx.android.synthetic.main.activity_video_detail.*
import java.text.SimpleDateFormat
import java.util.*

@SuppressLint("SimpleDateFormat")
/**
 * @author jascal
 * @time 2018/7/3
 * describe
 *  // set cover
val imageView = ImageView(this)
imageView.scaleType = ImageView.ScaleType.CENTER_CROP
GlideApp.with(this)
.load(DEMO_COVER)
.centerCrop()
.into(imageView)
mViewPlayer.setCover(imageView)

// set uri
mViewPlayer.setData(DEMO_URI)
 */
class VideoDetailActivity : BaseActivity(), VideoDetailContract.View {
    companion object {
        const val IMG_TRANSITION = "IMG_TRANSITION"
        const val TRANSITION = "TRANSITION"
    }

    // 第一次调用的时候初始化
    private val mPresenter by lazy { VideoDetailPresenter() }
    private val mAdapter by lazy { VideoDetailAdapter(this, itemList) }
    private val mFormat by lazy { SimpleDateFormat("yyyyMMddHHmmss"); }

    // Item 详细数据
    private lateinit var itemData: DiscoverBean.Issue.Item
    private var itemList = ArrayList<DiscoverBean.Issue.Item>()
    private var isPlay: Boolean = false
    private var isPause: Boolean = false
    private var isTransition: Boolean = false
    private var transition: Transition? = null
    private var mMaterialHeader: MaterialHeader? = null

    override fun layoutID(): Int = R.layout.activity_video_detail

    // 初始化 View
    override fun initView() {
        mPresenter.attachView(this)
        //过渡动画
        initTransition()
        initVideoViewConfig()

        mRecyclerView.layoutManager = LinearLayoutManager(this)
        mRecyclerView.adapter = mAdapter

        //设置相关视频 Item 的点击事件
        mAdapter.setOnItemDetailClick { mPresenter.loadVideoInfo(it) }

        //状态栏透明和间距处理
        StatusBarUtil.immersive(this)
        StatusBarUtil.setPaddingSmart(this, mVideoPlayer)

        /***  下拉刷新  ***/
        //内容跟随偏移
        mRefreshLayout.setEnableHeaderTranslationContent(true)
        mRefreshLayout.setOnRefreshListener {
            //            loadVideoInfo()
            // reload
        }
        mMaterialHeader = mRefreshLayout.refreshHeader as MaterialHeader?
        //打开下拉刷新区域块背景:
        mMaterialHeader?.setShowBezierWave(true)
        //设置下拉刷新主题颜色
        mRefreshLayout.setPrimaryColorsId(R.color.titleColorBlack, R.color.titleColorBg)
    }


    // 初始化 VideoView 的配置
    private fun initVideoViewConfig() {
        // set cover
        val imageView = ImageView(this)
        imageView.scaleType = ImageView.ScaleType.CENTER_CROP
        GlideApp.with(this)
                .load(itemData.data?.cover?.feed)
                .centerCrop()
                .into(imageView)
        mVideoPlayer.setCover(imageView)
    }

    // 初始化数据
    override fun initData() {
        itemData = intent.getSerializableExtra(Constants.BUNDLE_VIDEO_DATA) as DiscoverBean.Issue.Item
        isTransition = intent.getBooleanExtra(TRANSITION, false)

        saveWatchVideoHistoryInfo(itemData)
    }

    // 保存观看记录
    private fun saveWatchVideoHistoryInfo(watchItem: DiscoverBean.Issue.Item) {
        //保存之前要先查询sp中是否有该value的记录，有则删除.这样保证搜索历史记录不会有重复条目
        val historyMap = WatchHistoryUtils.getAll(Constants.FILE_WATCH_HISTORY_NAME, MyApplication.context) as Map<*, *>
        for ((key, _) in historyMap) {
            if (watchItem == WatchHistoryUtils.getObject(Constants.FILE_WATCH_HISTORY_NAME, MyApplication.context, key as String)) {
                WatchHistoryUtils.remove(Constants.FILE_WATCH_HISTORY_NAME, MyApplication.context, key)
            }
        }
        WatchHistoryUtils.putObject(Constants.FILE_WATCH_HISTORY_NAME, MyApplication.context, watchItem, "" + mFormat.format(Date()))
    }


    override fun showLoading() {

    }

    override fun dismissLoading() {
        mRefreshLayout.finishRefresh()
    }

    // 设置播放视频 URL
    override fun setVideo(url: String) {
        Logger.d("playUrl:$url")
        mVideoPlayer.setData(url)
    }

    // 设置视频信息
    override fun setVideoInfo(itemInfo: DiscoverBean.Issue.Item) {
        itemData = itemInfo
        mAdapter.addData(itemInfo)
        // 请求相关的最新等视频
        mPresenter.requestRelatedVideo(itemInfo.data?.id ?: 0)
    }

    // 设置相关的数据视频
    override fun setRecentRelatedVideo(itemList: ArrayList<DiscoverBean.Issue.Item>) {
        mAdapter.addData(itemList)
        this.itemList = itemList
    }

    // 设置背景颜色
    override fun setBackground(url: String) {
        GlideApp.with(this)
                .load(url)
                .centerCrop()
                .format(DecodeFormat.PREFER_ARGB_8888)
                .transition(DrawableTransitionOptions().crossFade())
                .into(mVideoBackground)
    }

    // 设置错误信息
    override fun setErrorMsg(errorMsg: String) {

    }

    override fun onConfigurationChanged(newConfig: Configuration?) {
        super.onConfigurationChanged(newConfig)
        mVideoPlayer.onConfigurationChanged(newConfig)
    }

    // 加载视频信息
    fun loadVideoInfo() {
        mPresenter.loadVideoInfo(itemData)
    }

    override fun onBackPressed() {
//        GSYVideoPlayer.releaseAllVideos()
        if (isTransition && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) run {
            super.onBackPressed()
        } else {
            finish()
            overridePendingTransition(R.anim.anim_out, R.anim.anim_in)
        }
    }

    override fun onResume() {
        super.onResume()
        isPause = false
    }

    override fun onPause() {
        super.onPause()
        isPause = true
    }

    override fun onDestroy() {
        CleanLeakUtils.fixInputMethodManagerLeak(this)
        super.onDestroy()
//        GSYVideoPlayer.releaseAllVideos()
        mPresenter.detachView()
    }

    private fun initTransition() {
        if (isTransition && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            postponeEnterTransition()
            ViewCompat.setTransitionName(mVideoPlayer, IMG_TRANSITION)
            addTransitionListener()
            startPostponedEnterTransition()
        } else {
            loadVideoInfo()
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private fun addTransitionListener() {
        transition = window.sharedElementEnterTransition
        transition?.addListener(object : Transition.TransitionListener {
            override fun onTransitionResume(p0: Transition?) {
            }

            override fun onTransitionPause(p0: Transition?) {
            }

            override fun onTransitionCancel(p0: Transition?) {
            }

            override fun onTransitionStart(p0: Transition?) {
            }

            override fun onTransitionEnd(p0: Transition?) {
                Logger.d("onTransitionEnd()------")

                loadVideoInfo()
                transition?.removeListener(this)
            }

        })
    }
}