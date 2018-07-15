package com.jascal.tvp.custom

import android.content.Context
import android.os.Build
import android.os.Handler
import android.support.annotation.RequiresApi
import android.util.AttributeSet
import android.view.MotionEvent
import android.widget.FrameLayout

/**
 * @author jascal
 * @time 2018/7/10
 * describe base layout of GestureDetector
 */
@RequiresApi(Build.VERSION_CODES.M)
abstract class VideoPlayerLayout : FrameLayout {
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
    }

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    /**
     * if return false: ignore event
     * if return true: dispatch event to fun onInterceptTouchEvent()
     */
    override fun dispatchTouchEvent(motionEvent: MotionEvent?): Boolean {
        return super.dispatchTouchEvent(motionEvent)
    }

    private var startX = 0f
    private var startY = 0f
    private var isActing = false

    /**
     * if return true: dispatch event to this.onTouchEvent()
     * if return false: dispatch event to child
     * */
    override fun onInterceptTouchEvent(motionEvent: MotionEvent?): Boolean {
        var deltaX = 0f
        var deltaY = 0f
        when (motionEvent?.action) {
            MotionEvent.ACTION_DOWN -> {
                // reset
                isActing = false
                startX = motionEvent.x
                startY = motionEvent.y
                requestDisallowInterceptTouchEvent(false)
            }
            MotionEvent.ACTION_MOVE -> {
                deltaX = startX - motionEvent.x
                deltaY = startY - motionEvent.y
                if (Math.abs(deltaX) > 100 || Math.abs(deltaY) > 100) {
                    isActing = true
                    return true
                }
                sendMsg(MSG_SHOW_ACTIONBAR, 0)
            }
            MotionEvent.ACTION_CANCEL -> {
                sendMsg(MSG_DISMISS_ALL, MSG_DELAY)
                if (isActing) {
                    isActing = false
                    return true
                }
            }
            MotionEvent.ACTION_UP -> {
                sendMsg(MSG_DISMISS_ALL, MSG_DELAY)
                if (isActing) {
                    isActing = false
                    return true
                }
            }
        }
        return false
    }

    /**
     * solve the motionEvent
     * */
    override fun onTouchEvent(motionEvent: MotionEvent?): Boolean {
        motionEvent?.let {
            when (it.action) {
                MotionEvent.ACTION_DOWN -> {
                }
                MotionEvent.ACTION_MOVE -> {
                    val mLayoutWidth = getVideoWidth()
                    val mLayoutHeight = getVideoHeight()

                    val dy = (startY - it.y) / mLayoutHeight
                    if (startX > mLayoutWidth * 2 / 3.0) {
                        onVolumeChange(dy)
                        sendMsg(MSG_SHOW_VOLUME, 0)
                    } else if (startX < mLayoutWidth / 3.0) {
                        onBrightnessChange(dy)
                        sendMsg(MSG_SHOW_BRIGHTNESS, 0)
                    }
                    startX = motionEvent.x
                    startY = motionEvent.y
                }
                MotionEvent.ACTION_UP -> {
                    sendMsg(MSG_DISMISS_ALL, MSG_DELAY)
                }
                MotionEvent.ACTION_CANCEL -> {
                    sendMsg(MSG_DISMISS_ALL, MSG_DELAY)
                }
            }
        }
        return super.onTouchEvent(motionEvent)
    }

    protected var mHandler: Handler? = null

    abstract fun initView()

    abstract fun initHandler()

    protected fun sendMsg(what: Int, delay: Long) {
        if (what == MSG_DISMISS_ALL || what == MSG_DISMISS_ACTIONBAR
                || what == MSG_DISMISS_BRIGHTNESS || what == MSG_DISMISS_VOLUME) {
            mHandler?.removeMessages(what)
        }
        val msg = mHandler?.obtainMessage(what)
        mHandler?.sendMessageDelayed(msg, delay)
    }

    abstract fun onPrepared()

    abstract fun getVideoWidth(): Int

    abstract fun getVideoHeight(): Int

    abstract fun onVolumeChange(d: Float?)

    abstract fun onBrightnessChange(d: Float?)
}