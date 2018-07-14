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
    companion object {
        /**
         * before stream prepared, show loading only
         * */
        private const val STATE_LOADING = 0

        /**
         * when stream is prepared, show cover &tailBar
         * */
        private const val STATE_PREPARED = 1

        /**
         * default play state, show nothing
         * */
        private const val STATE_DEFAULT = 2

        /**
         * when play, pause, seekTo, show tailBar
         * */
        private const val STATE_ACTION = 3

        /**
         * brightness motionAction start, show brightnessBar
         * */
        private const val STATE_BRIGHTNESS = 4

        /**
         * volume motionAction start, show volume
         * */
        private const val STATE_VOLUME = 5
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
                    if (startX > mLayoutWidth * 2 / 3.0) {
                        onVolumeChange(dy)
                    } else if (startX < mLayoutWidth / 3.0) {
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

    protected fun newTimer(){

//        final Handler handler = new Handler() {
//            @Override
//            public void handleMessage(Message msg) {
//                switch (msg.what) {
//                    case WHAT:
//                    tv.setText(msg.obj + "");
//                    break;
//                }
//            }
//        };
//        //true 说明这个timer以daemon方式运行（优先级低，程序结束timer也自动结束）
//        Timer timer = new Timer(true);
//        TimerTask task = new TimerTask() {
//            public void run() {
//                //每次需要执行的代码放到这里面。
//                    Message message = new Message();
//                    message.what = WHAT;
//                    message.obj = System.currentTimeMillis();
//                    handler.sendMessage(message);
//            }
//        };
//
//        //以下是几种调度task的方法：
//
//        //time为Date类型：在指定时间执行一次。
//        timer.schedule(task, time);
//
//        //firstTime为Date类型,period为long，表示从firstTime时刻开始，每隔period毫秒执行一次。
//        timer.schedule(task, firstTime, period);
//
//        //delay 为long类型：从现在起过delay毫秒执行一次。
//        timer.schedule(task, delay);
//
//        //delay为long,period为long：从现在起过delay毫秒以后，每隔period毫秒执行一次。
//        timer.schedule(task, delay, period);
    }

    abstract fun onPrepared()

    abstract fun getVideoWidth(): Int

    abstract fun getVideoHeight(): Int

    abstract fun onVolumeChange(d: Float?)

    abstract fun onBrightnessChange(d: Float?)

}