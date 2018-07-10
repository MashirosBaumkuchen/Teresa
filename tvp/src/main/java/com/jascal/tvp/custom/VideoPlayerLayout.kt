package com.jascal.tvp.custom

import android.content.Context
import android.os.Build
import android.support.annotation.RequiresApi
import android.util.AttributeSet
import android.view.GestureDetector
import android.view.MotionEvent
import android.widget.FrameLayout
import com.jascal.tvp.utils.Logger

/**
 * @author jascal
 * @time 2018/7/10
 * describe base layout of GestureDetector
 */
@RequiresApi(Build.VERSION_CODES.M)
abstract class VideoPlayerLayout : FrameLayout, GestureDetector.OnGestureListener,
        GestureDetector.OnContextClickListener, GestureDetector.OnDoubleTapListener {

    private var gt: GestureDetector? = null

    constructor(context: Context) : super(context) {
        Logger.showLog("motion constructor(context: Context)")
        initGestureDetector()
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        Logger.showLog("motion constructor(context: Context, attrs: AttributeSet?)")
        initGestureDetector()
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        Logger.showLog("motion constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int)")
        initGestureDetector()
    }

    private fun initGestureDetector() {
//        Logger.showLog("motion initGestureDetector")
//        gt = GestureDetector(context, this).apply {
//            setOnDoubleTapListener(this@VideoPlayerLayout)
//            setContextClickListener(this@VideoPlayerLayout)
//            setIsLongpressEnabled(false)
//        }
//        setOnTouchListener { _, motionEvent ->
//            gt!!.onTouchEvent(motionEvent)
//        }
    }

    override fun onShowPress(p0: MotionEvent?) {
        Logger.showLog("onShowPress")
    }

    override fun onSingleTapUp(p0: MotionEvent?): Boolean {
        Logger.showLog("onSingleTapUp")
        return false
    }

    override fun onDown(p0: MotionEvent?): Boolean {
        Logger.showLog("onDown")
        return false

    }

    override fun onFling(p0: MotionEvent?, p1: MotionEvent?, p2: Float, p3: Float): Boolean {
        Logger.showLog("onFling")
        return false

    }

    override fun onScroll(p0: MotionEvent?, p1: MotionEvent?, p2: Float, p3: Float): Boolean {
        Logger.showLog("onScroll")
        return false

    }

    override fun onLongPress(p0: MotionEvent?) {
        Logger.showLog("onLongPress")
    }

    override fun onContextClick(p0: MotionEvent?): Boolean {
        Logger.showLog("onContextClick")
        return false

    }

    override fun onDoubleTap(p0: MotionEvent?): Boolean {
        Logger.showLog("onDoubleTap")
        return false

    }

    override fun onDoubleTapEvent(p0: MotionEvent?): Boolean {
        Logger.showLog("onDoubleTapEvent")
        return false

    }

    override fun onSingleTapConfirmed(p0: MotionEvent?): Boolean {
        Logger.showLog("onSingleTapConfirmed")
        return false

    }
}