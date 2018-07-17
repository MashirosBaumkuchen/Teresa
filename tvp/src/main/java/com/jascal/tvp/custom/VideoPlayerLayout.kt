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

/**
 * @author jascal
 * @time 2018/7/10
 * describe base layout of GestureDetector
 */
@Suppress("LeakingThis", "DEPRECATED_IDENTITY_EQUALS")
@RequiresApi(Build.VERSION_CODES.M)
abstract class VideoPlayerLayout : FrameLayout, GestureDetector.OnGestureListener {
    companion object {
        const val BEHAVIOR_PROGRESS = 0
        const val BEHAVIOR_VOLUME = 1
        const val BEHAVIOR_BRIGHTNESS = 2
    }

    private var mBehavior = -1

    private var mCurrentBrightness = 0f
    private var mMaxBrightness = 0f

    private var mCurrentVolume = 0f
    private var mMaxVolume = 0f

    private var mGesture: GestureDetector? = null
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

        mCurrentVolume = mAudioManager!!.getStreamVolume(AudioManager.STREAM_MUSIC).toFloat()
        mMaxVolume = mAudioManager!!.getStreamMaxVolume(AudioManager.STREAM_MUSIC).toFloat()

        mCurrentBrightness = ((context as Activity).window.attributes.screenBrightness * mMaxBrightness)
        mMaxBrightness = 255f
    }

    protected fun resetSetting() {
        updateVolume(mCurrentVolume, mMaxVolume, false)
        updateBrightness(mCurrentBrightness, mMaxBrightness, false)
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
        mGesture!!.onTouchEvent(motionEvent)
        when (motionEvent?.action) {
            MotionEvent.ACTION_CANCEL -> {
                endEvent(mBehavior)
            }
            MotionEvent.ACTION_UP -> {
                endEvent(mBehavior)
            }
            MotionEvent.ACTION_OUTSIDE -> {
                endEvent(mBehavior)
            }
        }
        return true
    }

    abstract fun endEvent(behavior: Int)

    override fun onDown(e: MotionEvent): Boolean {
        mBehavior = -1
        mCurrentVolume = mAudioManager!!.getStreamVolume(AudioManager.STREAM_MUSIC).toFloat()
        mCurrentBrightness = ((context as Activity).window.attributes.screenBrightness * mMaxBrightness).toInt().toFloat()
        return true
    }

    override fun onShowPress(e: MotionEvent) {
    }

    override fun onSingleTapUp(e: MotionEvent): Boolean {
        onEvent(e)
        return true
    }

    abstract fun onEvent(e: MotionEvent)

    protected fun getViewWindow(view: View): RectF {
        var location: IntArray = IntArray(2)
        view.getLocationOnScreen(location)
        return RectF(location[0].toFloat(), location[1].toFloat(), (location[0] + view.width).toFloat(),
                (location[1] + view.height).toFloat());
    }

    override fun onScroll(e1: MotionEvent, e2: MotionEvent,
                          distanceX: Float, distanceY: Float): Boolean {
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
                val delProgress = -(1.0f * distanceX / width * 480 * 1000).toInt()
                updateSeek(delProgress)
            }
            BEHAVIOR_BRIGHTNESS -> {
                if (Settings.System.getInt(context.contentResolver, Settings.System.SCREEN_BRIGHTNESS_MODE) === Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC) {
                    Settings.System.putInt(context.contentResolver, Settings.System.SCREEN_BRIGHTNESS_MODE,
                            Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL)
                }

                var progress = (mMaxBrightness * (distanceY / height) + mCurrentBrightness)

                // 控制调节临界范围
                if (progress <= 0) progress = 0f
                if (progress >= mMaxBrightness) progress = mMaxBrightness

                val window = (context as Activity).window
                val params = window.attributes
                params.screenBrightness = progress / mMaxBrightness
                window.attributes = params

                updateBrightness(progress, mMaxBrightness, true)
                mCurrentBrightness = progress
            }
            BEHAVIOR_VOLUME -> {
                var progress = mMaxVolume * (distanceY / height) + mCurrentVolume

                if (progress <= 0) progress = 0f
                if (progress >= mMaxVolume) progress = mMaxVolume

                mAudioManager!!.setStreamVolume(AudioManager.STREAM_MUSIC, Math.round(progress), 0)

                updateVolume(progress, mMaxVolume, true)
                mCurrentVolume = progress
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

    /**
     * update player seekTo UI
     * */
    abstract fun updateSeek(progress: Int)

    /**
     * update brightness UI
     * */
    abstract fun updateBrightness(brightness: Float, max: Float, show: Boolean)

    /**
     * update volume UI
     * */
    abstract fun updateVolume(volume: Float, max: Float, show: Boolean)

    /**
     * change player state
     * */
    abstract fun changePlayerState()

    /**
     * change bottom actionBar state
     * */
    abstract fun changeActionState()

    /**
     * control view dismiss & seekBar position
     * */
    protected var mHandler: Handler? = null

    abstract fun initView()

    abstract fun initHandler()

    protected fun sendMsg(what: Int, delay: Long) {
        val msg = mHandler?.obtainMessage(what)
        mHandler?.sendMessageDelayed(msg, delay)
    }

    abstract fun onPrepared()

}