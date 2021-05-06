package tech.codevil.tracne.ui.common

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import androidx.core.content.withStyledAttributes
import tech.codevil.tracne.R
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

/**
 * Created by kervin.decena on 05/04/2021.
 */
class FancySeekBar @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    interface Listener {
        fun onValueChanged(newValue: Int)
    }

    private var min = 0
    private var max = 10
    private var fillBarPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        color = Color.LTGRAY
    }
    private var knobPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        color = Color.MAGENTA
    }
    private var textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        color = Color.WHITE
        textAlign = Paint.Align.CENTER
    }
    private var barRectF = RectF(0f, 0f, 0f, 0f)
    private var fillRectF = RectF(0f, 0f, 0f, 0f)
    private var knobRadius: Float
    private val knobDiameter: Float get() = knobRadius * 2
    private val knobTouchRadius: Float get() = knobRadius * 1.2f
    private var knobX: Float = 0f
    private var knobY: Float = 0f
    private var dragKnob = false
    private var marksList: MutableList<Float> = mutableListOf()
    private var currentValueIndex = 0
    private var listener: Listener? = null

    val value get() = currentValueIndex

    init {
        knobRadius = dpToPx(16f)
        knobX = knobRadius
        knobY = knobRadius
        textPaint.textSize = knobRadius
        context.withStyledAttributes(attrs, R.styleable.FancySeekBar) {
            min = getInt(R.styleable.FancySeekBar_minValue, 0)
            max = getInt(R.styleable.FancySeekBar_maxValue, 10)
            currentValueIndex = getInt(R.styleable.FancySeekBar_initialValue, 0)
            fillBarPaint.color = getColor(R.styleable.FancySeekBar_barBackgroundColor, Color.LTGRAY)
            knobPaint.color = getColor(R.styleable.FancySeekBar_barForegroundColor, Color.MAGENTA)
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val heightSpecMode = MeasureSpec.getMode(heightMeasureSpec)
        if (heightSpecMode != MeasureSpec.EXACTLY) {
            setMeasuredDimension(widthMeasureSpec, knobDiameter.toInt())
        } else {
            setMeasuredDimension(widthMeasureSpec, heightMeasureSpec)
        }
    }


    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        if (width == 0 || height == 0) {
            return
        }
        barRectF.set(0f, 0f, w.toFloat(), knobDiameter)
        fillRectF.set(0f, 0f, 0f, knobDiameter)

        recomputePoints()
    }

    private fun recomputePoints() {
        marksList.clear()
        val step = (width - knobDiameter) / ((max - min).toFloat())
        for (i in min..max) marksList.add(knobRadius + ((i - min) * step))

        if (currentValueIndex in 0 until marksList.size) {
            knobX = marksList[value]
            postInvalidate()
        }
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas?.apply {
            //draw background
            drawRoundRect(barRectF, knobDiameter, knobDiameter, fillBarPaint)

            //draw foreground
            fillRectF.right = knobX + knobRadius
            drawRoundRect(fillRectF, knobDiameter, knobDiameter, knobPaint)

            //draw knob
            //drawCircle(knobX, knobY, knobRadius, knobPaint)

            drawText("${currentValueIndex + min}", knobX, knobY + knobRadius / 3, textPaint)
        }
    }

    override fun dispatchTouchEvent(event: MotionEvent?): Boolean {
        if (parent == null) {
            return super.dispatchTouchEvent(event)
        }
        when (event?.action) {
            MotionEvent.ACTION_DOWN -> parent.requestDisallowInterceptTouchEvent(true)
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> parent.requestDisallowInterceptTouchEvent(
                false
            )
        }
        return super.dispatchTouchEvent(event)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        Log.d(javaClass.simpleName, "onTouchEvent $event")
        when (event?.action) {
            MotionEvent.ACTION_DOWN -> {
                dragKnob = RectF(
                    knobX - knobTouchRadius,
                    knobY - knobTouchRadius,
                    knobX + knobTouchRadius,
                    knobY + knobTouchRadius
                ).contains(event.x, event.y)
                Log.d(javaClass.simpleName, "Action down, dragknob = $dragKnob")
                return true
            }
            MotionEvent.ACTION_MOVE -> {
                if (dragKnob) {
                    knobX = min(max(knobRadius, event.x), width - knobRadius)
                    setCurrentValueIndex(getNearestMarkIndex())
                    postInvalidate()
                }
                Log.d(javaClass.simpleName, "Action move")
                return true
            }
            MotionEvent.ACTION_UP -> {
                dragKnob = false
                knobX = event.x
                Log.d(javaClass.simpleName, "Action up")
                stickKnobToMark()
                return true
            }
        }
        return super.onTouchEvent(event)
    }

    private fun stickKnobToMark() {
        setCurrentValueIndex(getNearestMarkIndex())
        knobX = marksList[currentValueIndex]
        postInvalidate()
    }

    private fun getNearestMarkIndex(): Int {
        val x = knobX
        var diff = Float.MAX_VALUE
        var closestIndex = 0
        marksList.forEachIndexed { i, item ->
            if (abs(item - x) < diff) {
                closestIndex = i
                diff = abs(item - x)
                Log.d(javaClass.simpleName, "diff = $diff, i = $i")
            }
        }
        return closestIndex
    }

    private fun dpToPx(dp: Float): Float {
        return dp * resources.displayMetrics.density
    }

    fun setValue(value: Int) { //value is zero-indexed
        if (value in min until max) {
            setCurrentValueIndex(value - min)
            if ((value - min) in 0 until marksList.size) {
                knobX = marksList[value]
                postInvalidate()
            }
        }
    }

    fun setMinMax(min: Int = 0, max: Int = 10) {
        this.min = min
        this.max = max
        recomputePoints()
        postInvalidate()
    }

    fun setOnValueChangedListener(listener: Listener?) {
        this.listener = listener
        listener?.onValueChanged(currentValueIndex + min)
    }

    private fun setCurrentValueIndex(newValueIndex: Int) {
        if (listener == null) {
            currentValueIndex = newValueIndex
        } else if (currentValueIndex != newValueIndex) {
            currentValueIndex = newValueIndex
            listener?.onValueChanged(newValueIndex + min)
            Log.d(javaClass.simpleName, "onValueChanged ${newValueIndex + min}")
        }
    }
}