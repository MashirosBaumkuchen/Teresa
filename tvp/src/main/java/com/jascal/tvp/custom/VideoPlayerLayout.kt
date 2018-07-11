package com.jascal.tvp.custom

import android.content.Context
import android.os.Build
import android.support.annotation.RequiresApi
import android.util.AttributeSet
import android.view.MotionEvent
import android.widget.FrameLayout
import com.jascal.tvp.utils.Logger

/**
 * @author jascal
 * @time 2018/7/10
 * describe base layout of GestureDetector
 */
@RequiresApi(Build.VERSION_CODES.M)
abstract class VideoPlayerLayout : FrameLayout {
    constructor(context: Context) : super(context) {
        Logger.showLog("motion constructor(context: Context)")
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        Logger.showLog("motion constructor(context: Context, attrs: AttributeSet?)")
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        Logger.showLog("motion constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int)")
    }

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
        Logger.showLog("onInterceptTouchEvent")
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
        Logger.showLog("onTouchEvent")
        when (motionEvent?.action) {
            MotionEvent.ACTION_DOWN -> {
                Logger.showLog("ACTION_DOWN")
            }
            MotionEvent.ACTION_MOVE -> {
                Logger.showLog("ACTION_MOVE")
            }
            MotionEvent.ACTION_CANCEL -> {
                Logger.showLog("ACTION_CANCEL")
            }
            MotionEvent.ACTION_UP -> {
                Logger.showLog("ACTION_UP")
            }
        }
        return super.onTouchEvent(motionEvent)
    }


}