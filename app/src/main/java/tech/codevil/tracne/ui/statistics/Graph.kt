package tech.codevil.tracne.ui.statistics

import android.graphics.Color
import android.graphics.Paint
import android.os.Parcelable
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize

@Parcelize
data class Graph(
    val xMin: Int = 0, val xMax: Int = 10,
    val yMin: Int = 0, val yMax: Int = 10,
    var valuesMap: MutableMap<Int, Int>,
    val color: Int = Color.CYAN,
    val xLabels: Map<Int, String> = mapOf(),
    val yLabels: Map<Int, String> = mapOf(),
    val showBigMarkerAtX: Int = -1,
) : Parcelable {
    @IgnoredOnParcel
    val graphPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = this@Graph.color
        style = Paint.Style.STROKE
        strokeWidth = 5.0f
    }

    @IgnoredOnParcel
    val pointPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = this@Graph.color
        style = Paint.Style.FILL
    }

    @IgnoredOnParcel
    val bigPointPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = this@Graph.color
        style = Paint.Style.STROKE
        strokeWidth = 5.0f
    }

    @IgnoredOnParcel
    val shapePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = this@Graph.color
        style = Paint.Style.FILL
        alpha = 32
    }
}