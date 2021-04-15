package tech.codevil.tracne.ui.statistics

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import tech.codevil.tracne.common.util.Constants
import java.util.*

class MultipleGraphView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    data class Graph(
        val xMin: Int = 0, val xMax: Int = 10,
        val yMin: Int = 0, val yMax: Int = 10,
        var valuesMap: MutableMap<Int, Int>,
        val color: Int = Color.CYAN
    )
    {
        val graphPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = this@Graph.color
            style = Paint.Style.FILL
            alpha = 64
        }

        val pointPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = this@Graph.color
            style = Paint.Style.FILL
        }
    }

    var sleepValuesMap = mutableMapOf(
        1 to 6,
        2 to 6,
        3 to 7,
        4 to 7,
        5 to 8,
        6 to 7,
        7 to 7,
        8 to 8,
        9 to 9,
        10 to 12,
        11 to 9,
        12 to 10,
        13 to 6,
        14 to 5,
        15 to 7,
        16 to 13,
        17 to 8,
        18 to 7,
        19 to 1,
        20 to 8,
    )
    val sleepGraph = Graph(
        xMin = 1,
        xMax = 20,
        yMin = 0,
        yMax = 13,
        valuesMap = sleepValuesMap,
        color = Color.BLUE
    )

    var spotsValuesMap = mutableMapOf(
        1 to 0,
        2 to 0,
        3 to 0,
        4 to 2,
        5 to 3,
        6 to 3,
        7 to 0,
        8 to 0,
        9 to 1,
        10 to 3,
        11 to 3,
        12 to 2,
        13 to 2,
        14 to 19,
        15 to 20,
        16 to 10,
        17 to 19,
        18 to 9,
        19 to 0,
        20 to 0,
    )
    val spotsGraph = Graph(
        xMin = 1,
        xMax = 20,
        yMin = 0,
        yMax = 20,
        valuesMap = spotsValuesMap,
        color = Color.MAGENTA
    )


    val graphs = mutableListOf(sleepGraph, spotsGraph)
    val graphPaths = mutableListOf(Path(), Path())
    val pointsList = mutableListOf(mutableListOf<PointF>(), mutableListOf<PointF>())
    val pointRadius = 5.0f

    val linePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.DKGRAY
        style = Paint.Style.FILL
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        computePoints()
    }

    private fun computePoints() {
        val totalWidth = width - graphStart() - graphEnd()
        val totalHeight = height - graphTop() - graphBottom()
        graphs.forEachIndexed { index, graph ->
            val path = graphPaths[index]
            val points = pointsList[index]

            val spacingX = totalWidth / ((graph.xMax - graph.xMin).toFloat())
            val spacingY = totalHeight / ((graph.yMax - graph.yMin).toFloat())

            var prevX = graphStart()
            var prevY = height - graphBottom()

            path.reset()
            path.moveTo(prevX, prevY)
            points.clear()

            val xValues = (graph.xMin..graph.xMax).toList()
            xValues.forEachIndexed { i, x ->
                if (graph.valuesMap.containsKey(x)) {
                    val value = graph.valuesMap[x]!!
                    val pointX = graphStart() + (i * spacingX)
                    val pointY = height - graphBottom() - ((value - graph.yMin) * spacingY)
                    points.add(PointF(pointX, pointY))

                    if (path.isEmpty) {
                        path.lineTo(prevX, pointY)
                    } else {
                        path.cubicTo((prevX + pointX) / 2f, prevY, (prevX + pointX) / 2f, pointY, pointX, pointY)
                        //https://medium.com/@pranjalg2308/understanding-bezier-curve-in-android-and-moddinggraphview-library-a9b1f0f95cd0
                    }
                    prevX = pointX
                    prevY = pointY
                }
            }
            if (points.isNotEmpty()) {
                path.lineTo(points.last().x, height.toFloat() - graphBottom())
                path.lineTo(graphStart(), height.toFloat() - graphBottom())
                path.lineTo(graphStart(), points.first().y)
            }
            path.close()
        }


    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas?.apply {
            drawLine(
                graphStart(),
                height.toFloat() - graphBottom(),
                width.toFloat() - graphEnd(),
                height.toFloat() - graphBottom(),
                linePaint
            )
            graphs.forEachIndexed { i, graph ->
                drawPath(graphPaths[i], graph.graphPaint)
                pointsList[i].map { drawCircle(it.x, it.y, pointRadius, graph.pointPaint) }
            }
        }
    }

    private fun graphStart(): Float {
        return 50f
    }

    private fun graphEnd(): Float {
        return 50f
    }

    private fun graphTop(): Float {
        return 50f
    }

    private fun graphBottom(): Float {
        return 50f
    }

    fun setGraphs(graphs: List<Graph>) {
        this.graphs.clear()
        this.graphs.addAll(graphs)
        computePoints()
        postInvalidate()
    }

}