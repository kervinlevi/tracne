package tech.codevil.tracne.ui.statistics

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import tech.codevil.tracne.common.util.Constants
import java.util.*

/**
 * Created by kervin.decena on 12/04/2021.
 */
class GraphView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    val formatter = Constants.DAY_FORMAT
    val valuesMap = mapOf(
        "April 1, 2021" to 8,
        "April 2, 2021" to 9,
        "April 3, 2021" to 9,
        "April 4, 2021" to 6,
        "April 5, 2021" to 5,
        "April 6, 2021" to 6,
        "April 7, 2021" to 7,
        "April 8, 2021" to 8,
        "April 9, 2021" to 4,
        "April 10, 2021" to 4
    )
    val xValues = mutableListOf<String>()
    val xPositions = mutableListOf<Float>()
    val maxValue = 9
    var points = mutableListOf<PointF>()

    val pointPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.BLUE
        style = Paint.Style.FILL
    }
    val graphFill = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.BLUE
        style = Paint.Style.FILL
        alpha = 64
    }
    val xGuidePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.GRAY
        style = Paint.Style.FILL
        alpha = 64
    }

    val pointRadius = 10.0f
    val graphLinePath = Path()


    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        if (w > 0 && h > 0) {
            computePoints()
        } else {
            points = mutableListOf()
        }
    }

    private fun computePoints() {
        points = mutableListOf()

        xValues.clear()
        val today = Calendar.getInstance()
        val numberOfDays = today.getActualMaximum(Calendar.DAY_OF_MONTH)
        val iterateDate = Calendar.getInstance()
        for (i in 1..10) {
            iterateDate.set(Calendar.DAY_OF_MONTH, i)
            xValues.add(formatter.format(iterateDate.time))
            xPositions
        }

        val spacingX = width / ((xValues.size - 1).toFloat())
        val spacingY = height / ((maxValue - 1).toFloat())

        graphLinePath.reset()
        graphLinePath.moveTo(0f, height.toFloat())
        var prevX = 0f
        var prevY = height.toFloat()

        xPositions.clear()
        xValues.forEachIndexed { i, key ->
            xPositions.add(i * spacingX)
            if (valuesMap.containsKey(key)) {
                val x = i * spacingX
                val y = (maxValue - valuesMap[key]!!) * spacingY

                points.add(PointF(x, y))
                if (graphLinePath.isEmpty) {
                    graphLinePath.lineTo(0f, y)
                } else {
                    graphLinePath.cubicTo((prevX + x) / 2f, prevY, (prevX + x) / 2f, y, x, y)
                    //https://medium.com/@pranjalg2308/understanding-bezier-curve-in-android-and-moddinggraphview-library-a9b1f0f95cd0
                }
                prevX = x
                prevY = y
            }
        }
        if (points.isNotEmpty()) {
            graphLinePath.lineTo(points.last().x, height.toFloat())
            graphLinePath.lineTo(0f, height.toFloat())
            graphLinePath.lineTo(0f, points.first().y)
        }
        graphLinePath.close()
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas?.apply {
            xPositions.map { drawLine(it, 0f, it, height.toFloat(), xGuidePaint) }
            drawPath(graphLinePath, graphFill)
            points.map { drawCircle(it.x, it.y, pointRadius, pointPaint) }
        }
    }

}