package com.jascal.tvp.custom

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.media.MediaPlayer
import android.net.Uri
import android.os.Handler
import android.os.Message
import android.os.Parcelable
import android.util.AttributeSet
import android.view.*
import android.widget.*
import com.jascal.tvp.utils.Logger
import com.jascal.tvp.utils.ResUtil
import java.text.SimpleDateFormat
import java.util.*


class VideoPlayer : FrameLayout, View.OnClickListener {
    private var view: View? = null
    private var mPlayer: MediaPlayer? = null
    private var mSurfaceView: SurfaceView? = null
    private var mActionBar: RelativeLayout? = null
    private var mCover: RelativeLayout? = null
    private var mStart: ImageView? = null
    private var mCollapse: ImageView? = null
    private var mSeekBar: SeekBar? = null
    private var mDuration: TextView? = null
    private var mUri: String? = null
    private var mCoverImg:View?=null

    companion object {
        const val DEMO_URI: String = "http://baobab.kaiyanapp.com/api/v1/playUrl?vid=113514&resourceType=video&editionType=default&source=aliyun"
        const val DEMO_COVER: String = "http://img.kaiyanapp.com/cbe8ea9ae48ff855ef3d51a16733ede3.png?imageMogr2/quality/60/format/jpg"
    }

    constructor(context: Context) : super(context) {
        init(context, DEMO_URI)
        Logger.showLog("    constructor(context: Context) : super(context) {\n")
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init(context, DEMO_URI)
        Logger.showLog("    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {\n")
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init(context, DEMO_URI)
        Logger.showLog("    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {\n")
    }

    constructor(context: Context, uri: String) : super(context) {
        init(context, uri)
        Logger.showLog("    constructor(context: Context, uri: String) : super(context) {\n")
    }

    constructor(context: Context, attrs: AttributeSet?, uri: String) : super(context, attrs) {
        init(context, uri)
        Logger.showLog("    constructor(context: Context, attrs: AttributeSet?, uri: String) : super(context, attrs) {\n")
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int, uri: String) : super(context, attrs, defStyleAttr) {
        init(context, uri)
        Logger.showLog("    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int, uri: String) : super(context, attrs, defStyleAttr) {\n")
    }

    fun setCover(cover:View){
        mCover?.let {
            this.mCoverImg = cover
            resolveCover(cover)
        }
    }

    private fun init(context: Context, uri: String) {
        Logger.showLog("init start")
        val layoutId = ResUtil.getLayoutId(context, "layout_player")
        view = View.inflate(context, layoutId, this)
        mSurfaceView = findViewById(ResUtil.getId(context, "mSurfaceView"))
        mActionBar = findViewById(ResUtil.getId(context, "mActionBar"))
        mStart = findViewById(ResUtil.getId(context, "mStart"))
        mCollapse = findViewById(ResUtil.getId(context, "mCollapse"))
        mSeekBar = findViewById(ResUtil.getId(context, "mSeekBar"))
        mDuration = findViewById(ResUtil.getId(context, "mDuration"))
        mCover = findViewById(ResUtil.getId(context, "mCover"))
        mUri = uri
        initPlayer()
        initView()
    }

    private fun initPlayer() {
        Logger.showLog("initPlayer")
        mPlayer = MediaPlayer().apply {
            setDataSource(context, Uri.parse(mUri))
            prepare()
            setOnPreparedListener {
                it.isLooping = true
            }
        }
    }

    private fun initView() {
        Logger.showLog("initView")
        mSurfaceView?.let {
            it.holder.addCallback(MediaPlayerCallBack())
        }

        mCover?.setOnClickListener(this)
        mStart?.setOnClickListener(this)
        mCollapse?.setOnClickListener(this)

        mDuration?.let {
            it.text = getFormatTime()
        }

        mSeekBar?.let {
            it.max = mPlayer!!.duration
            it.progress = 0
            it.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(seekbar: SeekBar?, progress: Int, fromUser: Boolean) {
                    if (!fromUser) return
                    mPlayer?.seekTo(progress)
                }

                override fun onStartTrackingTouch(seekbar: SeekBar?) {
                    mPlayer?.pause()
                }

                override fun onStopTrackingTouch(seekbar: SeekBar?) {
                    mPlayer?.start()
                }
            })
        }
        val dThread = DelayThread(100)
        dThread.start()
    }

    override fun onConfigurationChanged(newConfig: Configuration?) {
        super.onConfigurationChanged(newConfig)
        Logger.showLog("    override fun onConfigurationChanged(newConfig: Configuration?) {\n")
        if (this.resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
            setVideoParams(this.mPlayer!!, isLand = false)
        } else if (this.resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            setVideoParams(this.mPlayer!!, isLand = true)
        }
    }

    override fun onSaveInstanceState(): Parcelable {
        Logger.showLog("onSaveInstanceState in viewPlayer")
        mPlayer?.let {
            if (it.isPlaying) {
                it.pause()
            }
        }
        return super.onSaveInstanceState()
    }

    override fun onRestoreInstanceState(state: Parcelable?) {
        Logger.showLog("onRestoreInstanceState in viewPlayer")
        super.onRestoreInstanceState(state)
        mPlayer?.let {
            if (!it.isPlaying) {
                it.start()
            }
        }
    }



    private fun setVideoParams(mediaPlayer: MediaPlayer, isLand: Boolean) {
        val flLayoutParams = layoutParams
        val sfLayoutParams = mSurfaceView!!.layoutParams

        val screenWidth = resources.displayMetrics.widthPixels.toFloat()
        var screenHeight = resources.displayMetrics.widthPixels * 9f / 16f

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
                it.pause()
            } else {
                it.start()
            }
        }
    }

    private fun getFormatTime(): String {
        val date = Date(mPlayer!!.duration.toLong())
        val simpleDateFormat = SimpleDateFormat("mm:ss")
        val totalTime = simpleDateFormat.format(date)
        val currentPosition = simpleDateFormat.format(mPlayer!!.currentPosition)
        return "$currentPosition/$totalTime"
    }

    override fun onClick(view: View?) {
        when (view!!.id) {
            ResUtil.getId(context, "mCollapse") -> {
                Logger.showLog("            ResUtil.getId(context, \"mCollapse\")->{\n")
                if (resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
                    //变成竖屏
                    (context as Activity).requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
                } else if (resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
                    //变成横屏了
                    (context as Activity).requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
                }

            }
            ResUtil.getId(context, "mStart") -> {
                Logger.showLog("            ResUtil.getId(context, \"mStart\") -> {\n")
                changePlayState()
            }
            ResUtil.getId(context,"mCover")->{
                Logger.showLog("            ResUtil.getId(context, \"mCover\") -> {\n")
                mCover?.visibility = View.GONE
                mPlayer?.start()
            }
        }
    }

    private fun resolveCover(cover:View){
        mCover?.let {
            it.removeAllViews()
            it.addView(cover)
            val lp:ViewGroup.LayoutParams = cover.layoutParams
            lp.height = ViewGroup.LayoutParams.MATCH_PARENT
            lp.width = ViewGroup.LayoutParams.MATCH_PARENT
            cover.layoutParams = lp
        }
    }

    private var mHandler: Handler = @SuppressLint("HandlerLeak")
    object : Handler() {
        override fun handleMessage(msg: Message) {
            mSeekBar!!.progress = (mPlayer!!.currentPosition)
            mDuration?.let {
                it.text = getFormatTime()
            }

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