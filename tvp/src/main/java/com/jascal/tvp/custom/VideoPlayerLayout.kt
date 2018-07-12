package com.jascal.tvp.custom

import android.content.Context
import android.os.Build
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
            }
            MotionEvent.ACTION_CANCEL -> {
                if (isActing) {
                    isActing = false
                    return true
                }
            }
            MotionEvent.ACTION_UP -> {
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
                    if (startX > mLayoutWidth * 3.0 / 4) {
                        onVolumeChange(dy)
                    } else if (startX < mLayoutWidth / 4.0) {
                        onBrightnessChange(dy)
                    }

                    startX = motionEvent.x
                    startY = motionEvent.y
                }
                MotionEvent.ACTION_UP -> {

                }
                MotionEvent.ACTION_CANCEL -> {

                }
            }
        }
        return super.onTouchEvent(motionEvent)
    }

    abstract fun onPrepared()
    abstract fun getVideoWidth():Int
    abstract fun getVideoHeight():Int
    abstract fun onVolumeChange(d: Float?)
    abstract fun onBrightnessChange(d: Float?)

}