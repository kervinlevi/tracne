package tech.codevil.tracne.ui.statistics

import android.content.Context
import android.graphics.*
import android.text.TextPaint
import android.util.AttributeSet
import android.view.View
import androidx.core.content.res.ResourcesCompat
import tech.codevil.tracne.R
import tech.codevil.tracne.common.util.Extensions.dp
import tech.codevil.tracne.common.util.Extensions.sp

class ParameterGraphView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : View(context, attrs, defStyleAttr) {

    private var graph1: Graph? = null
    private val graph1Path = Path()
    private val graph1Shape = Path()
    private val points1List = mutableListOf<PointF>()
    private val y1Points = mutableListOf<Float>()
    private var bigPoint1: PointF? = null
    private var y1Axis = 0.0f

    private var graph2: Graph? = null
    private val graph2Path = Path()
    private val graph2Shape = Path()
    private val points2List = mutableListOf<PointF>()
    private val y2Points = mutableListOf<Float>()
    private var bigPoint2: PointF? = null
    private var y2Axis = 0.0f

    private var spacingX = 0.0f
    private val xPoints = mutableListOf<Float>()
    private var xValues = listOf<Int>()
    private var xAxis = 0.0f
    private var bigPointAtX = -1

    private val pointRadius = 5.0f
    private val bigPointRadius = 10.0f

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

    val y1LabelTextPaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
        textSize = 10f.sp(context)
        typeface = ResourcesCompat.getFont(context, R.font.open_sans_semibold)
        textAlign = Paint.Align.LEFT
    }

    val y1LabelUnderlinePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeWidth = 1f.dp(context)
        pathEffect = DashPathEffect(floatArrayOf(8f.dp(context), 8f.dp(context)), 0f)
    }

    val y2LabelTextPaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
        textSize = 10f.sp(context)
        typeface = ResourcesCompat.getFont(context, R.font.open_sans_semibold)
        textAlign = Paint.Align.RIGHT
    }

    val y2LabelUnderlinePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeWidth = 1f.dp(context)
        pathEffect = DashPathEffect(floatArrayOf(8f.dp(context), 8f.dp(context)), 0f)
    }

    val xLabelPaddingTop by lazy { 8f.dp(context) }
    val yLabelPaddingBottom by lazy { 2f.dp(context) }
    val yLabelUnderlineWidth by lazy { 100f.dp(context) }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        computePoints()
    }

    private fun computePoints() {
        xAxis = height - graphBottom() - getXLabelBound().height() - xLabelPaddingTop
        y1Axis = graphStart()
        y2Axis = width - graphEnd()

        val totalWidth = width - graphStart() - graphEnd()
        val totalHeight = xAxis - graphTop()

        graph1?.let { graph ->
            val path = graph1Path
            val points = points1List
            val shape = graph1Shape

            spacingX = totalWidth / ((graph.xMax - graph.xMin).toFloat())
            val spacingY = totalHeight / ((graph.yMax - graph.yMin).toFloat())
            bigPointAtX = graph.showBigMarkerAtX

            var prevX = graphStart()
            var prevY = xAxis

            path.reset()
            shape.reset()
            points.clear()
            xPoints.clear()
            y1Points.clear()

            xValues = (graph.xMin..graph.xMax).toList()
            xValues.forEachIndexed { i, x ->
                if (graph.valuesMap.containsKey(x)) {
                    val value = graph.valuesMap[x]!!
                    val pointX = graphStart() + (i * spacingX)
                    val pointY = xAxis - ((value - graph.yMin) * spacingY)
                    points.add(PointF(pointX, pointY))
                    if (bigPointAtX == x) {
                        bigPoint1 = PointF(pointX, pointY)
                    }

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
                xPoints.add((i * spacingX) + graphStart())
            }
            val yValues = (graph.yMin..graph.yMax).toList()
            yValues.forEachIndexed { i, y -> y1Points.add(xAxis - (i * spacingY)) }
            shape.addPath(path)
            if (points.isNotEmpty()) {
                shape.lineTo(points.last().x, xAxis)
                shape.lineTo(points.first().x, xAxis)
                shape.close()
            }
            y1LabelTextPaint.color = graph.color

            val gradient = LinearGradient(graphStart(), 0f, graphStart() + yLabelUnderlineWidth,
                0f,
                graph.color,
                Color.argb(0,
                    Color.red(graph.color),
                    Color.green(graph.color),
                    Color.blue(graph.color)),
                Shader.TileMode.CLAMP)
            y1LabelUnderlinePaint.setShader(gradient)
        }

        graph2?.let { graph ->
            val path = graph2Path
            val points = points2List
            val shape = graph2Shape

            val spacingY = totalHeight / ((graph.yMax - graph.yMin).toFloat())

            var prevX = graphStart()
            var prevY = xAxis

            path.reset()
            shape.reset()
            points.clear()
            y2Points.clear()

            xValues.forEachIndexed { i, x ->
                if (graph.valuesMap.containsKey(x)) {
                    val value = graph.valuesMap[x]!!
                    val pointX = graphStart() + (i * spacingX)
                    val pointY = xAxis - ((value - graph.yMin) * spacingY)
                    points.add(PointF(pointX, pointY))
                    if (bigPointAtX == x) {
                        bigPoint2 = PointF(pointX, pointY)
                    }

                    if (path.isEmpty) {
                        path.moveTo(pointX, pointY)
                    } else {
                        path.cubicTo((prevX + pointX) / 2f,
                            prevY,
                            (prevX + pointX) / 2f,
                            pointY,
                            pointX,
                            pointY)
                    }
                    prevX = pointX
                    prevY = pointY
                }
            }
            val yValues = (graph.yMin..graph.yMax).toList()
            yValues.forEachIndexed { i, y -> y2Points.add(xAxis - (i * spacingY)) }
            shape.addPath(path)
            if (points.isNotEmpty()) {
                shape.lineTo(points.last().x, xAxis)
                shape.lineTo(points.first().x, xAxis)
                shape.close()
            }
            y2LabelTextPaint.color = graph.color

            val gradient =
                LinearGradient(width - graphEnd(), 0f, width - graphEnd() - yLabelUnderlineWidth,
                    0f,
                    graph.color,
                    Color.argb(0,
                        Color.red(graph.color),
                        Color.green(graph.color),
                        Color.blue(graph.color)),
                    Shader.TileMode.CLAMP)
            y2LabelUnderlinePaint.setShader(gradient)
        }

    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas?.apply {
            drawLine(graphStart(), xAxis, width.toFloat() - graphEnd(), xAxis, linePaint)

            graph1?.let { graph ->
                val lineStartX = graphStart()
                val lineEndX = graphStart() + yLabelUnderlineWidth
                (graph.yMin..graph.yMax).forEachIndexed { i, y ->
                    val label = graph.yLabels[y]
                    val yPoint = y1Points.getOrNull(i)
                    if (label != null && yPoint != null) {
                        drawText(label, y1Axis, yPoint - yLabelPaddingBottom, y1LabelTextPaint)
                        if (yPoint != xAxis) {
                            drawLine(lineStartX, yPoint, lineEndX, yPoint, y1LabelUnderlinePaint)
                        }
                    }
                }

                drawPath(graph1Shape, graph.shapePaint)
                drawPath(graph1Path, graph.graphPaint)
                points1List.map { drawCircle(it.x, it.y, pointRadius, graph.pointPaint) }

                val xLabelPositionY = xAxis + getXLabelBound().height() + xLabelPaddingTop
                xPoints.forEachIndexed { i, x ->
                    val label = graph.xLabels[i]
                    if (label != null) {
                        drawPoint(x, xAxis, pointsPaint)
                        xLabelTextPaint.textAlign = when (i) {
                            0 -> Paint.Align.LEFT
                            xPoints.size - 1 -> Paint.Align.RIGHT
                            else -> Paint.Align.CENTER
                        }
                        drawText(label, x, xLabelPositionY, xLabelTextPaint)
                    }
                }
                val point = bigPoint1
                point?.let {
                    drawCircle(it.x, it.y, bigPointRadius, bigPointsPaint)
                    drawCircle(it.x, it.y, bigPointRadius, graph.bigPointPaint)
                }
            }

            graph2?.let { graph ->
                val lineStartX = width - graphEnd()
                val lineEndX = lineStartX - yLabelUnderlineWidth

                (graph.yMin..graph.yMax).forEachIndexed { i, y ->
                    val label = graph.yLabels[y]
                    val yPoint = y2Points.getOrNull(i)
                    if (label != null && yPoint != null) {
                        drawText(label, y2Axis, yPoint - yLabelPaddingBottom, y2LabelTextPaint)
                        if (yPoint != xAxis) {
                            drawLine(lineStartX, yPoint, lineEndX, yPoint, y2LabelUnderlinePaint)
                        }
                    }
                }

                drawPath(graph2Shape, graph.shapePaint)
                drawPath(graph2Path, graph.graphPaint)
                points2List.map { drawCircle(it.x, it.y, pointRadius, graph.pointPaint) }

                val point = bigPoint2
                point?.let {
                    drawCircle(it.x, it.y, bigPointRadius, bigPointsPaint)
                    drawCircle(it.x, it.y, bigPointRadius, graph.bigPointPaint)
                }
            }

        }
    }

    private fun getXLabelBound(): Rect {
        val xLabelBounds = Rect()
        xLabelTextPaint.getTextBounds("X", 0, 1, xLabelBounds)
        return xLabelBounds
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

    fun setGraph1(graph: Graph) {
        this.graph1 = graph

        graph1Path.reset()
        points1List.clear()
        graph1Shape.reset()
        xPoints.clear()
        y1Points.clear()
        bigPoint1 = null

        computePoints()
        postInvalidate()
    }

    fun clearGraph1() {
        graph1 = null
        graph1Path.reset()
        points1List.clear()
        graph1Shape.reset()
        xPoints.clear()
        y1Points.clear()
        bigPoint1 = null
        postInvalidate()
    }

    fun setGraph2(graph: Graph) {
        this.graph2 = graph

        graph2Path.reset()
        points2List.clear()
        graph2Shape.reset()
        xPoints.clear()
        y2Points.clear()
        bigPoint2 = null

        computePoints()
        postInvalidate()
    }

    fun clearGraph2() {
        graph2 = null
        graph2Path.reset()
        points2List.clear()
        graph2Shape.reset()
        y2Points.clear()
        bigPoint2 = null

        postInvalidate()
    }

}