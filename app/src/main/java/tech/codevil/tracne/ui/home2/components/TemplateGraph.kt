package tech.codevil.tracne.ui.home2.components

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import tech.codevil.tracne.model.Template
import tech.codevil.tracne.ui.statistics.Graph

@Parcelize
data class TemplateGraph(
    val template: Template, val graph: Graph,
    val startTimestamp: Long, val endTimestamp: Long,
) : Parcelable