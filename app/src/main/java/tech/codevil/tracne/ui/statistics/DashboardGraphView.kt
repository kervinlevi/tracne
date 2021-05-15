package tech.codevil.tracne.ui.statistics

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View

class DashboardGraphView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    val graphs = mutableListOf<MultipleGraphView.Graph>()
    val graphPaths = mutableListOf<Path>()
    val graphShapes = mutableListOf<Path>()
    val pointsList = mutableListOf<MutableList<PointF>>()
    val pointRadius = 5.0f

    val linePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.LTGRAY
        style = Paint.Style.FILL
        strokeWidth = 5.0f
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
            val shape = graphShapes[index]

            val spacingX = totalWidth / ((graph.xMax - graph.xMin).toFloat())
            val spacingY = totalHeight / ((graph.yMax - graph.yMin).toFloat())

            var prevX = graphStart()
            var prevY = height - graphBottom()

            path.reset()
            shape.reset()
            points.clear()

            val xValues = (graph.xMin..graph.xMax).toList()
            xValues.forEachIndexed { i, x ->
                if (graph.valuesMap.containsKey(x)) {
                    val value = graph.valuesMap[x]!!
                    val pointX = graphStart() + (i * spacingX)
                    val pointY = height - graphBottom() - ((value - graph.yMin) * spacingY)
                    points.add(PointF(pointX, pointY))

                    if (path.isEmpty) {
                        path.moveTo(pointX, pointY)
                    } else {
                        path.cubicTo((prevX + pointX) / 2f, prevY, (prevX + pointX) / 2f, pointY, pointX, pointY)
                        //https://medium.com/@pranjalg2308/understanding-bezier-curve-in-android-and-moddinggraphview-library-a9b1f0f95cd0
                    }
                    prevX = pointX
                    prevY = pointY
                }
            }
            shape.addPath(path)
            if (points.isNotEmpty()) {
                shape.lineTo(points.last().x, height.toFloat() - graphBottom())
                shape.lineTo(points.first().x, height.toFloat() - graphBottom())
                shape.close()
            }
        }


    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas?.apply {
            graphs.forEachIndexed { i, graph ->
                drawPath(graphShapes[i], graph.shapePaint)
                drawPath(graphPaths[i], graph.graphPaint)
                pointsList[i].map { drawCircle(it.x, it.y, pointRadius, graph.pointPaint) }
            }
            drawLine(
                graphStart(),
                height.toFloat() - graphBottom(),
                width.toFloat() - graphEnd(),
                height.toFloat() - graphBottom(),
                linePaint
            )
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

    fun setGraphs(graphs: List<MultipleGraphView.Graph>) {
        this.graphs.clear()
        this.graphs.addAll(graphs)

        graphPaths.clear()
        pointsList.clear()
        graphShapes.clear()
        repeat(this.graphs.size) {
            graphPaths.add(Path())
            pointsList.add(mutableListOf())
            graphShapes.add(Path())
        }

        computePoints()
        postInvalidate()
    }

    fun clearGraphs() {
        graphs.clear()
        graphPaths.clear()
        pointsList.clear()
        graphShapes.clear()
        postInvalidate()
    }

}