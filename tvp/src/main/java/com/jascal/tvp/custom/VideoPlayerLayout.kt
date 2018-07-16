package com.jascal.tvp.custom

import android.app.Activity
import android.content.Context
import android.graphics.RectF
import android.media.AudioManager
import android.os.Build
import android.os.Handler
import android.provider.Settings
import android.support.annotation.RequiresApi
import android.util.AttributeSet
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.widget.FrameLayout
import com.jascal.tvp.utils.Logger

/**
 * @author jascal
 * @time 2018/7/10
 * describe base layout of GestureDetector
 */
@Suppress("LeakingThis", "DEPRECATED_IDENTITY_EQUALS")
@RequiresApi(Build.VERSION_CODES.M)
abstract class VideoPlayerLayout : FrameLayout, GestureDetector.OnGestureListener {
    companion object {
        /**
         * before stream prepared, show loading only
         * */
        const val STATE_LOADING = 0

        /**
         * when stream is prepared, show cover &tailBar
         * */
        const val STATE_PREPARED = 1

        /**
         * default play state, show nothing
         * */
        const val STATE_DEFAULT = 2

        /**
         * when play, pause, seekTo, show tailBar
         * */
        const val STATE_ACTION = 3

        /**
         * brightness motionAction start, show brightnessBar
         * */
        const val STATE_BRIGHTNESS = 4

        /**
         * volume motionAction start, show volume
         * */
        const val STATE_VOLUME = 5

        const val MSG_DISMISS_BRIGHTNESS = 10
        const val MSG_DISMISS_VOLUME = 11
        const val MSG_DISMISS_ACTIONBAR = 12
        const val MSG_DISMISS_ALL = 13

        const val MSG_SHOW_BRIGHTNESS = 14
        const val MSG_SHOW_VOLUME = 15
        const val MSG_SHOW_ACTIONBAR = 16

        const val MSG_DELAY = 3000L

        const val BEHAVIOR_PROGRESS = 47
        const val BEHAVIOR_VOLUME = 48
        const val BEHAVIOR_BRIGHTNESS = 49
    }

    private var mBehavior = -1
    private var mCurrentBrightness = 0
    private var mCurrentVolume = 0
    private var mGesture: GestureDetector? = null
    private var mMaxBrightness = -1
    private var mMaxVolume = 0
    private var mAudioManager: AudioManager? = null

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init()
    }

    private fun init() {
        mGesture = GestureDetector(context, this)
        mAudioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager

        mCurrentVolume = mAudioManager!!.getStreamVolume(AudioManager.STREAM_MUSIC)
        mMaxVolume = mAudioManager!!.getStreamMaxVolume(AudioManager.STREAM_MUSIC)

        mCurrentBrightness = ((context as Activity).window.attributes.screenBrightness * mMaxBrightness).toInt()
        mMaxBrightness = 255
    }

    protected fun resetSetting() {
        updateVolume(mCurrentVolume, mMaxVolume)
        updateBrightness(mCurrentBrightness, mMaxBrightness)
    }

    /**
     * if return false: ignore event
     * if return true: dispatch event to fun onInterceptTouchEvent()
     */
    override fun dispatchTouchEvent(motionEvent: MotionEvent?): Boolean {
        return super.dispatchTouchEvent(motionEvent)
    }

    /**
     * if return true: dispatch event to this.onTouchEvent()
     * if return false: dispatch event to child
     * */
    override fun onInterceptTouchEvent(motionEvent: MotionEvent?): Boolean {
        return true
    }

    /**
     * solve the motionEvent
     * */
    override fun onTouchEvent(motionEvent: MotionEvent?): Boolean {
        Logger.showLog("onTouchEvent")
        return mGesture!!.onTouchEvent(motionEvent)
    }

    override fun onDown(e: MotionEvent): Boolean {
        Logger.showLog("onDown")
        mBehavior = -1
        mCurrentVolume = mAudioManager!!.getStreamVolume(AudioManager.STREAM_MUSIC)
        mCurrentBrightness = ((context as Activity).window.attributes.screenBrightness * mMaxBrightness).toInt()
        return true
    }

    override fun onShowPress(e: MotionEvent) {
        Logger.showLog("onShowPress")
    }

    override fun onSingleTapUp(e: MotionEvent): Boolean {
        Logger.showLog("onSingleTapUp")
        changeActionState()
//        changePlayerState()
        onEvent(e)
        return true
    }

    abstract fun onEvent(e:MotionEvent)

    protected fun getViewWindow(view: View): RectF {
        var location: IntArray = IntArray(2)
        view.getLocationOnScreen(location)
        return RectF(location[0].toFloat(), location[1].toFloat(), (location[0] + view.width).toFloat(),
                (location[1] + view.height).toFloat());
    }

    override fun onScroll(e1: MotionEvent, e2: MotionEvent,
                          distanceX: Float, distanceY: Float): Boolean {
        Logger.showLog("onScroll")
        if (width <= 0 || height <= 0) return false
        if (mBehavior < 0) {
            val moveX = e2.x - e1.x
            val moveY = e2.y - e1.y
            mBehavior = when {
                Math.abs(moveX) > Math.abs(moveY) -> BEHAVIOR_PROGRESS
                e1.x <= width / 2 -> BEHAVIOR_BRIGHTNESS
                else -> BEHAVIOR_VOLUME
            }
        }
        when (mBehavior) {
            BEHAVIOR_PROGRESS -> {
                Logger.showLog("progress")
                val delProgress = -(1.0f * distanceX / width * 480 * 1000).toInt()
                updateSeek(delProgress)
            }
            BEHAVIOR_BRIGHTNESS -> {
                Logger.showLog("brightness")

                if (Settings.System.getInt(context.contentResolver, Settings.System.SCREEN_BRIGHTNESS_MODE) === Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC) {
                    Settings.System.putInt(context.contentResolver, Settings.System.SCREEN_BRIGHTNESS_MODE,
                            Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL)
                }

                var progress = (mMaxBrightness * (distanceY / height) + mCurrentBrightness).toInt()

                // 控制调节临界范围
                if (progress <= 0) progress = 0
                if (progress >= mMaxBrightness) progress = mMaxBrightness

                val window = (context as Activity).window
                val params = window.attributes
                params.screenBrightness = progress / mMaxBrightness.toFloat()
                window.attributes = params

                updateBrightness(progress, mMaxBrightness)
                mCurrentBrightness = progress
                Logger.showLog("progress = $progress, max = $mMaxBrightness")
            }
            BEHAVIOR_VOLUME -> {
                Logger.showLog("volume")

                var progress = mMaxVolume * (distanceY * 3 / height) + mCurrentVolume

                // 控制调节临界范围
                if (progress <= 0) progress = 0f
                if (progress >= mMaxVolume) progress = mMaxVolume.toFloat()

                mAudioManager!!.setStreamVolume(AudioManager.STREAM_MUSIC, Math.round(progress), 0)
                updateVolume(Math.round(progress), mMaxVolume)
                mCurrentVolume = progress.toInt()
                Logger.showLog("progress = ${Math.round(progress)}, max = $mMaxVolume")
            }
        }
        return false
    }

    override fun onLongPress(e: MotionEvent) {
        //
    }

    override fun onFling(e1: MotionEvent, e2: MotionEvent, velocityX: Float,
                         velocityY: Float): Boolean {
        return true
    }

    abstract fun updateSeek(progress: Int)

    abstract fun updateBrightness(brightness: Int, max: Int)

    abstract fun updateVolume(volume: Int, max: Int)

    abstract fun changePlayerState()

    abstract fun changeActionState()

    protected var mHandler: Handler? = null

    abstract fun initView()

    abstract fun initHandler()

    protected fun sendMsg(what: Int, delay: Long) {
        val msg = mHandler?.obtainMessage(what)
        mHandler?.sendMessageDelayed(msg, delay)
    }

    abstract fun onPrepared()

}