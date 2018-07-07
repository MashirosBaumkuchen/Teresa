package com.jascal.tvp.custom

import android.annotation.SuppressLint
import android.content.Context
import android.media.MediaPlayer
import android.net.Uri
import android.os.Handler
import android.os.Message
import android.util.AttributeSet
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.view.View
import android.widget.*
import com.jascal.tvp.utils.ResUtil
import java.text.SimpleDateFormat
import java.util.*

class VideoPlayer : FrameLayout {
    private var view: View? = null
    private var mPlayer: MediaPlayer? = null
    private var mSurfaceView: SurfaceView? = null
    private var mActionBar: RelativeLayout? = null
    private var mStart: ImageView? = null
    private var mCollapse: ImageView? = null
    private var mSeekBar: SeekBar? = null
    private var mDuration: TextView? = null
    private var mUri: String? = null

    companion object {
        private const val DEMO_URI: String = "http://baobab.kaiyanapp.com/api/v1/playUrl?vid=14914&editionType=default&source=ucloud"
    }

    constructor(context: Context) : super(context) {
        init(context, DEMO_URI)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init(context, DEMO_URI)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init(context, DEMO_URI)
    }

    constructor(context: Context, uri: String) : super(context) {
        init(context, uri)
    }

    constructor(context: Context, attrs: AttributeSet?, uri: String) : super(context, attrs) {
        init(context, uri)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int, uri: String) : super(context, attrs, defStyleAttr) {
        init(context, uri)
    }

    private fun init(context: Context, uri: String) {
        val layoutId = ResUtil.getLayoutId(context, "activity_main")
        view = View.inflate(context, layoutId, this)
        mSurfaceView = findViewById(ResUtil.getId(context, "mSurfaceView"))
        mActionBar = findViewById(ResUtil.getId(context, "mActionBar"))
        mStart = findViewById(ResUtil.getId(context, "mStart"))
        mCollapse = findViewById(ResUtil.getId(context, "mCollapse"))
        mSeekBar = findViewById(ResUtil.getId(context, "mSeekBar"))
        mDuration = findViewById(ResUtil.getId(context, "mDuration"))
        mUri = uri
        initPlayer()
        initView()
    }

    private fun initPlayer() {
        mPlayer = MediaPlayer().apply {
            setDataSource(context, Uri.parse(mUri))
            prepare()
            setOnPreparedListener {
                it.start()
                it.isLooping = true
            }
            setOnVideoSizeChangedListener { _: MediaPlayer, width: Int, height: Int ->
                view!!.layoutParams.height = height
                view!!.layoutParams.width = width
            }
        }
    }

    private fun initView() {
        mSurfaceView?.let {
            it.holder.addCallback(MediaPlayerCallBack())
        }

        mStart?.let {
            it.setOnClickListener {
                changePlayState()
            }
        }

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

    private var mHandler: Handler = @SuppressLint("HandlerLeak")
    object : Handler() {
        override fun handleMessage(msg: Message) {
            mSeekBar!!.progress = (mPlayer!!.currentPosition)
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

        }
    }

}