package com.jascal.tvp.custom

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.media.MediaPlayer
import android.net.Uri
import android.os.Build
import android.os.Handler
import android.os.Message
import android.os.Parcelable
import android.support.annotation.RequiresApi
import android.util.AttributeSet
import android.view.*
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.SeekBar
import android.widget.TextView
import com.jascal.tvp.utils.Logger
import com.jascal.tvp.utils.ResUtil
import kotlinx.android.synthetic.main.layout_player.view.*
import java.text.SimpleDateFormat

/**
 * @author jascal
 * @time 2018/7/10
 * describe a impl of VideoPlayerLayout
 */
@RequiresApi(Build.VERSION_CODES.M)
class VideoPlayer : VideoPlayerLayout, View.OnClickListener, SeekBar.OnSeekBarChangeListener {

    companion object {
        private const val ICON_START = "ic_action_start"
        private const val ICON_PAUSE = "ic_action_pause"
        private const val ICON_COLLAPSE = "ic_action_collapse"
        private const val ICON_EXPAND = "ic_action_expand"
    }

    private var view: View? = null
    private var mPlayer: MediaPlayer? = null
    private var mSurfaceView: SurfaceView? = null
    private var mActionBar: RelativeLayout? = null
    private var mCoverContainer: RelativeLayout? = null
    private var mStart: ImageView? = null
    private var mCollapse: ImageView? = null
    private var mSeekBar: SeekBar? = null
    private var mDuration: TextView? = null
    private var mUri: String? = null
    private var mCover: View? = null

    private var mCurrentPosition = 0
    private var mTotalTime = 0
    private val mSimpleDateFormat: SimpleDateFormat by lazy { SimpleDateFormat("mm:ss") }

    constructor(context: Context) : super(context) {
        initView()
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        initView()
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        initView()
    }

    fun setCover(cover: View) {
        this.mCover = cover
    }

    fun setData(uri: String) {
        this.mUri = uri
        mPlayer?.let {
            Logger.showLog("setData uri")
            it.setDataSource(context, Uri.parse(mUri))
            it.prepareAsync()
            it.isLooping = true
            it.setOnPreparedListener {
                mProgress.visibility = View.GONE
                onPrepared()
            }
        }
    }

    private fun initView() {
        mPlayer = MediaPlayer()

        val layoutId = ResUtil.getLayoutId(context, "layout_player")
        view = View.inflate(context, layoutId, this)
        mSurfaceView = findViewById(ResUtil.getId(context, "mSurfaceView"))
        mSurfaceView?.holder?.addCallback(MediaPlayerCallBack())

        mActionBar = findViewById(ResUtil.getId(context, "mActionBar"))
        mStart = findViewById(ResUtil.getId(context, "mStart"))
        mCollapse = findViewById(ResUtil.getId(context, "mCollapse"))
        mSeekBar = findViewById(ResUtil.getId(context, "mSeekBar"))
        mDuration = findViewById(ResUtil.getId(context, "mDuration"))
        mCoverContainer = findViewById(ResUtil.getId(context, "mCoverContainer"))
    }

    override fun onPrepared() {
        resolveCover(mCover!!)
        setVideoParams(mPlayer!!, false)

        mSurfaceView?.setOnClickListener(this)
        mCoverContainer?.setOnClickListener(this)
        mStart?.setOnClickListener(this)
        mCollapse?.setOnClickListener(this)

        mSeekBar?.let {
            it.max = mPlayer!!.duration
            it.progress = 0
            it.setOnSeekBarChangeListener(this)
        }

        val dThread = DelayThread(100)
        dThread.start()
    }

    override fun onProgressChanged(seekbar: SeekBar?, progress: Int, fromUser: Boolean) {
        if (!fromUser) return
        mPlayer?.seekTo(progress)
        mCurrentPosition = progress
    }

    override fun onStartTrackingTouch(seekbar: SeekBar?) {
        pause()
    }

    override fun onStopTrackingTouch(seekbar: SeekBar?) {
        play()
    }

    override fun onConfigurationChanged(newConfig: Configuration?) {
        super.onConfigurationChanged(newConfig)
        if (this.resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
            setVideoParams(this.mPlayer!!, isLand = false)
        } else if (this.resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            setVideoParams(this.mPlayer!!, isLand = true)
        }
    }

    override fun onSaveInstanceState(): Parcelable {
        mPlayer?.let {
            it.pause()
            mStart!!.setImageResource(ResUtil.getMipmapId(context, ICON_START))
            mCurrentPosition = it.currentPosition
        }
        return super.onSaveInstanceState()
    }

    override fun onRestoreInstanceState(state: Parcelable?) {
        super.onRestoreInstanceState(state)
        play()
    }

    override fun onWindowFocusChanged(hasWindowFocus: Boolean) {
        super.onWindowFocusChanged(hasWindowFocus)
        if (hasWindowFocus) {
            mPlayer?.seekTo(mCurrentPosition)
        }
    }

    private fun updatePlayTime() {
        mDuration?.let {
            it.text = getFormatTime()
        }
    }

    private fun collapse() {
        mCollapse?.let {
            (context as Activity).requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
            it.setImageResource(ResUtil.getMipmapId(context, ICON_EXPAND))
        }
    }

    private fun expand() {
        mCollapse?.let {
            (context as Activity).requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
            it.setImageResource(ResUtil.getMipmapId(context, ICON_COLLAPSE))
        }
    }

    private fun play() {
        mCoverContainer?.let {
            if (it.visibility == View.VISIBLE) {
                it.visibility = View.GONE
            }
        }
        mPlayer?.let {
            it.start()
            mStart!!.setImageResource(ResUtil.getMipmapId(context, ICON_PAUSE))
        }
    }

    private fun pause() {
        mPlayer?.let {
            it.pause()
            mStart!!.setImageResource(ResUtil.getMipmapId(context, ICON_START))
        }
    }

    private var isFirst = true
    private var por = 0f
    private fun setVideoParams(mediaPlayer: MediaPlayer, isLand: Boolean) {
        val flLayoutParams = layoutParams
        val sfLayoutParams = mSurfaceView!!.layoutParams

        var screenWidth = resources.displayMetrics.widthPixels.toFloat()
        var screenHeight = 0f
        if (isFirst) {
            screenHeight = layoutParams.height.toFloat()
            por = screenWidth / screenHeight
            isFirst = false
        } else {
            screenHeight = resources.displayMetrics.widthPixels * 9f / 16f
        }

        (context as Activity).window.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        if (isLand) {
            screenHeight = resources.displayMetrics.heightPixels.toFloat()
            (context as Activity).window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        }

        flLayoutParams.width = screenWidth.toInt()
        flLayoutParams.height = screenHeight.toInt()

        val videoWidth = mediaPlayer.videoWidth
        val videoHeight = mediaPlayer.videoHeight

        val videoPor = (videoWidth / videoHeight).toFloat()
        val screenPor = screenWidth / screenHeight

        //16:9    16:12
        if (screenPor > videoPor) {
            sfLayoutParams.height = screenHeight.toInt()
            sfLayoutParams.width = (screenHeight * screenPor).toInt()
        } else {
            //16:9  19:9
            sfLayoutParams.width = screenWidth.toInt()
            sfLayoutParams.height = (screenWidth / screenPor).toInt()
        }

        layoutParams = flLayoutParams
        mSurfaceView!!.layoutParams = sfLayoutParams
    }

    private fun changePlayState() {
        mPlayer?.let {
            if (it.isPlaying) {
                pause()
            } else {
                play()
            }
        }
    }

    private fun changeOrientationState() {
        if (resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
            expand()
        } else if (resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            collapse()
        }
    }

    private fun getFormatTime(): String {
        mTotalTime = mPlayer!!.duration
        mCurrentPosition = mPlayer!!.currentPosition
        val str = mSimpleDateFormat.format(mTotalTime)
        val str1 = mSimpleDateFormat.format(mCurrentPosition)
        return "$str1/$str"
    }

    override fun onClick(view: View?) {
        when (view!!.id) {
            ResUtil.getId(context, "mCollapse") -> {
                changeOrientationState()
            }
            ResUtil.getId(context, "mStart") -> {
                changePlayState()
            }
            ResUtil.getId(context, "mCoverContainer") -> {
                changePlayState()
            }
            ResUtil.getId(context, "mSurfaceView") -> {
                changePlayState()
            }
        }
    }

    override fun getVideoWidth(): Int {
        return mSurfaceView!!.layoutParams.width
    }

    override fun getVideoHeight(): Int {
        return mSurfaceView!!.layoutParams.height
    }

    override fun onBrightnessChange(d: Float?) {
        Logger.showLog("onBrightnessChange, dy = $d")
    }

    override fun onVolumeChange(d: Float?) {
        Logger.showLog("onVolumeChange, dy = $d")
    }


    private fun resolveCover(cover: View) {
        mCoverContainer?.let {
            it.removeAllViews()
            it.addView(cover)
            val lp: ViewGroup.LayoutParams = cover.layoutParams
            lp.height = ViewGroup.LayoutParams.MATCH_PARENT
            lp.width = ViewGroup.LayoutParams.MATCH_PARENT
            cover.layoutParams = lp
        }
    }

    private var mHandler: Handler = @SuppressLint("HandlerLeak")
    object : Handler() {
        override fun handleMessage(msg: Message) {
            mSeekBar!!.progress = (mPlayer!!.currentPosition)
            updatePlayTime()
        }
    }

    inner class DelayThread(private var milliseconds: Int) : Thread() {
        override fun run() {
            while (true) {
                try {
                    Thread.sleep(milliseconds.toLong())
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }
                mHandler.sendEmptyMessage(0)
            }
        }
    }

    private inner class MediaPlayerCallBack : SurfaceHolder.Callback {
        override fun surfaceCreated(holder: SurfaceHolder) {
            mPlayer!!.setDisplay(holder)
        }

        override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {

        }

        override fun surfaceDestroyed(holder: SurfaceHolder) {
            mPlayer!!.pause()
        }
    }

}