package com.jascal.tvp.custom

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import com.jascal.tvp.utils.ResUtil

/**
 * @author jascal
 * @time 2018/7/12
 * describe seekBar in videoPlayer
 */
class VerticalSeekBar : View {
    private var mProgress = 0f
    private var mRadius = 0f
    private var mX = 0f
    private var mY = 0f
    private var mLineWidth = 0f
    private var mLineHeight = 0f
    private val mPaint: Paint by lazy { Paint() }
    private val mPointColor: Int by lazy { ResUtil.getColorId(context, "colorPoint") }
    private val mLineColor: Int by lazy { ResUtil.getColorId(context, "colorLine") }

    private var min = 0f
    private var max = 100f

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        setMeasuredDimension(measuredWidth, measuredHeight)

        val left = measuredWidth * 0.25f
        val right = measuredWidth * 0.75f

        mRadius = measuredWidth * 0.25f

        val top = mRadius
        val bottom = measuredHeight - mRadius

        mLineWidth = right - left
        mLineHeight = bottom - top

        mX = measuredWidth / 2.0f

        mY = measuredHeight - mRadius - (measuredHeight - 2 * mRadius) * mProgress / max
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        mY = measuredHeight - mRadius - (measuredHeight - 2 * mRadius) * mProgress / max

        mPaint.isAntiAlias = true
        mPaint.style = Paint.Style.FILL
        mPaint.color = mLineColor

        canvas?.drawLine(mX, mRadius, mX, measuredHeight - mRadius, mPaint)

        mPaint.color = mPointColor

        canvas?.drawCircle(mX, mY, mRadius / 2, mPaint)

        mPaint.reset()
    }

    fun setMin(min: Float) {
        this.min = min
    }

    fun setMax(max: Float) {
        this.max = max
    }

    fun setProgress(progress: Float) {
        this.mProgress = progress
        invalidate()
    }

}