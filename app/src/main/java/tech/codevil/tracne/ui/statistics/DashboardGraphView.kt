package tech.codevil.tracne.ui.statistics

import android.content.Context
import android.graphics.*
import android.text.TextPaint
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import androidx.core.content.res.ResourcesCompat
import tech.codevil.tracne.R
import tech.codevil.tracne.common.util.Extensions.sp


class DashboardGraphView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : View(context, attrs, defStyleAttr) {

    private var graph: MultipleGraphView.Graph? = null //TODO: make single graph
    private val graphPath = Path()
    private val graphShape = Path()
    private val pointsList = mutableListOf<PointF>()
    private val pointRadius = 5.0f
    private val bigPointRadius = 10.0f
    private val xPoints = mutableListOf<Float>()
    private val yPoints = mutableListOf<Float>()
    private var xAxis = 0.0f

    val linePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.LTGRAY
        style = Paint.Style.FILL
        strokeWidth = 5.0f
    }

    val pointsPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.BLACK
        style = Paint.Style.FILL
        strokeWidth = 5.0f
    }

    val bigPointsPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.WHITE
        style = Paint.Style.FILL
        strokeWidth = 5.0f
    }

    val xLabelTextPaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.BLACK
        textSize = 10f.sp(context)
        typeface = ResourcesCompat.getFont(context, R.font.open_sans_light)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        computePoints()
    }

    private fun computePoints() {
        val totalWidth = width - graphStart() - graphEnd()
        val totalHeight = height - graphTop() - graphBottom()

        val bounds = Rect()
        xLabelTextPaint.getTextBounds("X", 0, 1, bounds)
        xAxis = height - graphBottom() - bounds.height()

        graph?.let { graph ->
            val path = graphPath
            val points = pointsList
            val shape = graphShape

            val spacingX = totalWidth / ((graph.xMax - graph.xMin).toFloat())
            val spacingY = totalHeight / ((graph.yMax - graph.yMin).toFloat())

            var prevX = graphStart()
            var prevY = xAxis

            path.reset()
            shape.reset()
            points.clear()
            xPoints.clear()
            yPoints.clear()

            val xValues = (graph.xMin..graph.xMax).toList()
            xValues.forEachIndexed { i, x ->
                if (graph.valuesMap.containsKey(x)) {
                    val value = graph.valuesMap[x]!!
                    val pointX = graphStart() + (i * spacingX)
                    val pointY = xAxis - ((value - graph.yMin) * spacingY)
                    points.add(PointF(pointX, pointY))

                    if (path.isEmpty) {
                        path.moveTo(pointX, pointY)
                    } else {
                        path.cubicTo((prevX + pointX) / 2f,
                            prevY,
                            (prevX + pointX) / 2f,
                            pointY,
                            pointX,
                            pointY)
                        //https://medium.com/@pranjalg2308/understanding-bezier-curve-in-android-and-moddinggraphview-library-a9b1f0f95cd0
                    }
                    prevX = pointX
                    prevY = pointY
                }
                xPoints.add(i * spacingX)
            }
            val yValues = (graph.yMin..graph.yMax).toList()
            yValues.forEachIndexed { i, y -> yPoints.add(i * spacingY) }
            shape.addPath(path)
            if (points.isNotEmpty()) {
                shape.lineTo(points.last().x, xAxis)
                shape.lineTo(points.first().x, xAxis)
                shape.close()
            }
        }
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas?.apply {
            drawLine(graphStart(), xAxis, width.toFloat() - graphEnd(), xAxis, linePaint)
            graph?.let { graph ->
                drawPath(graphShape, graph.shapePaint)
                drawPath(graphPath, graph.graphPaint)
                pointsList.map { drawCircle(it.x, it.y, pointRadius, graph.pointPaint) }

                xPoints.forEachIndexed { i, x ->
                    val label = graph.xLabels[i]
                    if (label != null) {
                        drawPoint(x, xAxis, pointsPaint)
                        xLabelTextPaint.textAlign = when (i) {
                            0 -> Paint.Align.LEFT
                            xPoints.size - 1 -> Paint.Align.RIGHT
                            else -> Paint.Align.CENTER
                        }
                        drawText(label, x, height.toFloat(), xLabelTextPaint)
                    }
                }
                if (pointsList.isNotEmpty()) {
                    val bigPoint = pointsList.last()
                    drawCircle(bigPoint.x, bigPoint.y, bigPointRadius, bigPointsPaint)
                    drawCircle(bigPoint.x, bigPoint.y, bigPointRadius, graph.bigPointPaint)
                }
            }

        }
    }

    private fun graphStart(): Float {
        return paddingStart.toFloat()
    }

    private fun graphEnd(): Float {
        return paddingEnd.toFloat()
    }

    private fun graphTop(): Float {
        return paddingTop.toFloat()
    }

    private fun graphBottom(): Float {
        return paddingBottom.toFloat()
    }

    fun setGraph(graph: MultipleGraphView.Graph) {
        this.graph = graph

        graphPath.reset()
        pointsList.clear()
        graphShape.reset()
        xPoints.clear()
        yPoints.clear()

        computePoints()
        postInvalidate()
    }

    fun clearGraphs() {
        graphPath.reset()
        pointsList.clear()
        graphShape.reset()
        postInvalidate()
        xPoints.clear()
        yPoints.clear()
    }

}