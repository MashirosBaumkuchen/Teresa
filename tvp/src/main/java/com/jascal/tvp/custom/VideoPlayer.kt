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
import android.widget.*
import com.jascal.tvp.utils.Logger
import com.jascal.tvp.utils.ResUtil
import java.text.SimpleDateFormat
import android.content.Context.AUDIO_SERVICE
import android.media.AudioManager


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

        private const val MIN_BRIGHTNESS = 0.2f

        private const val MSG_RESET_PROGRESS = 20
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

    private var mProgress: ProgressBar? = null
    private var mBrightSeekBar: VerticalSeekBar? = null
    private var mVolumeSeekBar: VerticalSeekBar? = null

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
                mProgress?.visibility = View.GONE
                onPrepared()
            }
        }
    }

    override fun initView() {
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

        mProgress = findViewById(ResUtil.getId(context, "mProgress"))
        mBrightSeekBar = findViewById(ResUtil.getId(context, "mBrightSeekBar"))

        mVolumeSeekBar = findViewById(ResUtil.getId(context, "mVolumeSeekBar"))

//        mActionBar?.visibility = View.GONE
//        mBrightSeekBar?.visibility = View.GONE
//        mVolumeSeekBar?.visibility = View.GONE

        initHandler()
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

        resetSetting()

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
        Logger.showLog("target!!!!")
        when (view!!.id) {
            ResUtil.getId(context, "mCollapse") -> {
                changeOrientationState()
            }
            ResUtil.getId(context, "mStart") -> {
                changePlayerState()
            }
            ResUtil.getId(context, "mCoverContainer") -> {
                changePlayerState()
            }
            ResUtil.getId(context, "mSurfaceView") -> {
                changePlayerState()
            }
        }
    }

    override fun updateSeek(progress: Int) {
        Logger.showLog("current is ${mPlayer?.currentPosition}, progress is $progress")
        mPlayer?.let {
            it.seekTo(it.currentPosition+progress)
            mSeekBar?.progress = it.currentPosition+progress
        }
    }

    override fun updateBrightness(brightness: Int, max: Int) {
        mBrightSeekBar?.setMax(max.toFloat())
        mBrightSeekBar?.setProgress(brightness.toFloat())
    }

    override fun updateVolume(volume: Int, max: Int) {
        mVolumeSeekBar?.setMax(max.toFloat())
        mVolumeSeekBar?.setProgress(volume.toFloat())
    }

    override fun changeActionState(e:MotionEvent) {
        if (mActionBar?.visibility == View.VISIBLE) {
            mActionBar?.visibility = View.GONE
        } else {
            mActionBar?.visibility = View.VISIBLE
        }
    }

    override fun changePlayerState() {
        mPlayer?.let {
            if (it.isPlaying) {
                pause()
            } else {
                play()
            }
        }
    }

    override fun initHandler() {
        mHandler = @SuppressLint("HandlerLeak")
        object : Handler() {
            override fun handleMessage(msg: Message) {
                when (msg.what) {
                    MSG_RESET_PROGRESS -> {
                        mSeekBar!!.progress = (mPlayer!!.currentPosition)
                        updatePlayTime()
                    }
//                    MSG_DISMISS_ALL -> {
//                        mActionBar?.visibility = View.GONE
//                        mBrightSeekBar?.visibility = View.GONE
//                        mVolumeSeekBar?.visibility = View.GONE
//                    }
//                    MSG_SHOW_VOLUME -> {
//                        mVolumeSeekBar?.visibility = View.VISIBLE
//                    }
//                    MSG_SHOW_BRIGHTNESS -> {
//                        mBrightSeekBar?.visibility = View.VISIBLE
//                    }
//                    MSG_SHOW_ACTIONBAR -> {
//                        mActionBar?.visibility = View.VISIBLE
//                    }
                }
            }
        }
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

    inner class DelayThread(private var milliseconds: Int) : Thread() {
        override fun run() {
            while (true) {
                try {
                    Thread.sleep(milliseconds.toLong())
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }
                mHandler?.sendEmptyMessage(MSG_RESET_PROGRESS)
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