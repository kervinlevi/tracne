package tech.codevil.tracne.ui.statistics

import android.content.Context
import android.graphics.*
import android.os.Parcelable
import android.util.AttributeSet
import android.view.View
import kotlinx.parcelize.Parcelize
import tech.codevil.tracne.common.util.Constants
import java.util.*

class MultipleGraphView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    @Parcelize
    data class Graph(
        val xMin: Int = 0, val xMax: Int = 10,
        val yMin: Int = 0, val yMax: Int = 10,
        var valuesMap: MutableMap<Int, Int>,
        val color: Int = Color.CYAN
    ) : Parcelable {
        val graphPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = this@Graph.color
            style = Paint.Style.STROKE
            strokeWidth = 5.0f
//            alpha = 64
        }

        val pointPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = this@Graph.color
            style = Paint.Style.FILL
        }
    }
    val graphs = mutableListOf<Graph>()
    val graphPaths = mutableListOf<Path>()
    val pointsList = mutableListOf<MutableList<PointF>>()
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

            val spacingX = totalWidth / ((graph.xMax - graph.xMin).toFloat()) //TODO: confirm - 1
            val spacingY = totalHeight / ((graph.yMax - graph.yMin).toFloat())

            var prevX = graphStart()
            var prevY = height - graphBottom()

            path.reset()
//            path.moveTo(prevX, prevY)
            points.clear()

            val xValues = (graph.xMin..graph.xMax).toList()
            xValues.forEachIndexed { i, x ->
                if (graph.valuesMap.containsKey(x)) {
                    val value = graph.valuesMap[x]!!
                    val pointX = graphStart() + (i * spacingX)
                    val pointY = height - graphBottom() - ((value - graph.yMin) * spacingY)
                    points.add(PointF(pointX, pointY))

                    if (path.isEmpty) {
//                        path.lineTo(prevX, pointY)
                        path.moveTo(pointX, pointY)
                    } else {
                        path.cubicTo((prevX + pointX) / 2f, prevY, (prevX + pointX) / 2f, pointY, pointX, pointY)
                        //https://medium.com/@pranjalg2308/understanding-bezier-curve-in-android-and-moddinggraphview-library-a9b1f0f95cd0
                    }
                    prevX = pointX
                    prevY = pointY
                }
            }
//            if (points.isNotEmpty()) {
//                path.lineTo(points.last().x, height.toFloat() - graphBottom())
//                path.lineTo(graphStart(), height.toFloat() - graphBottom())
//                path.lineTo(graphStart(), points.first().y)
//            }
//            path.close()
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

    fun setGraphs(graphs: List<Graph>) {
        this.graphs.clear()
        this.graphs.addAll(graphs)

        graphPaths.clear()
        pointsList.clear()
        repeat(this.graphs.size) {
            graphPaths.add(Path())
            pointsList.add(mutableListOf())
        }

        computePoints()
        postInvalidate()
    }

    fun clearGraphs() {
        this.graphs.clear()
        graphPaths.clear()
        pointsList.clear()
        postInvalidate()
    }

}