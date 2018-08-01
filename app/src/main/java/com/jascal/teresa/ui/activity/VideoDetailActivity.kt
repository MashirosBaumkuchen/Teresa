package com.jascal.teresa.ui.activity

import android.annotation.SuppressLint
import android.content.res.Configuration
import android.os.Build
import android.support.v7.widget.LinearLayoutManager
import android.widget.ImageView
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
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_video_detail.*
import java.text.SimpleDateFormat
import java.util.*

@SuppressLint("SimpleDateFormat")
/**
 * @author jascal
 * @time 2018/7/3
 * describe todo replace glide to picasso
 *          todo a
 *          todo a
 *          todo a...
 *          aaaa...
 */
class VideoDetailActivity : BaseActivity(), VideoDetailContract.View {
    companion object {
        const val IMG_TRANSITION = "IMG_TRANSITION"
        const val TRANSITION = "TRANSITION"
    }

    private val mPresenter by lazy { VideoDetailPresenter() }
    private val mAdapter by lazy { VideoDetailAdapter(this, itemList) }
    private val mFormat by lazy { SimpleDateFormat("yyyyMMddHHmmss"); }

    private lateinit var itemData: DiscoverBean.Issue.Item
    private var itemList = ArrayList<DiscoverBean.Issue.Item>()
    private var isTransition: Boolean = false

    override fun layoutID(): Int = R.layout.activity_video_detail

    override fun initView() {
        mPresenter.attachView(this)
        setCover()

        mRecyclerView.layoutManager = LinearLayoutManager(this)
        mRecyclerView.adapter = mAdapter

        mAdapter.setOnItemDetailClick {
            //TODO
            mPresenter.loadVideoInfo(it)
        }

        StatusBarUtil.immersive(this)
        StatusBarUtil.setPaddingSmart(this, mVideoPlayer)
        loadVideoInfo()
    }


    private fun setCover() {
        val imageView = ImageView(this)
        imageView.scaleType = ImageView.ScaleType.CENTER_CROP
        GlideApp.with(this)
                .load(itemData.data?.cover?.feed)
                .centerCrop()
                .into(imageView)
        mVideoPlayer.setCover(imageView)
    }

    override fun initData() {
        itemData = intent.getSerializableExtra(Constants.BUNDLE_VIDEO_DATA) as DiscoverBean.Issue.Item
        isTransition = intent.getBooleanExtra(TRANSITION, false)

        saveWatchVideoHistoryInfo(itemData)
    }

    private fun saveWatchVideoHistoryInfo(watchItem: DiscoverBean.Issue.Item) {
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

    }

    override fun setVideo(url: String) {
        mVideoPlayer.setData(url)
    }

    override fun setVideoInfo(itemInfo: DiscoverBean.Issue.Item) {
        itemData = itemInfo
        mAdapter.addData(itemInfo)
        mPresenter.requestRelatedVideo(itemInfo.data?.id ?: 0)
    }

    override fun setRecentRelatedVideo(itemList: ArrayList<DiscoverBean.Issue.Item>) {
        mAdapter.addData(itemList)
        this.itemList = itemList
    }

    override fun setErrorMsg(errorMsg: String) {

    }

    override fun onConfigurationChanged(newConfig: Configuration?) {
        super.onConfigurationChanged(newConfig)
        mVideoPlayer.onConfigurationChanged(newConfig)
    }

    private fun loadVideoInfo() {
        mPresenter.loadVideoInfo(itemData)
    }

    override fun onBackPressed() {
        mVideoPlayer.releaseVideo()
        if (isTransition && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) run {
            super.onBackPressed()
        } else {
            finish()
            overridePendingTransition(R.anim.anim_out, R.anim.anim_in)
        }
    }

    override fun onDestroy() {
        CleanLeakUtils.fixInputMethodManagerLeak(this)
        super.onDestroy()
        mVideoPlayer.releaseVideo()
        mPresenter.detachView()
    }
}