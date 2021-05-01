package tech.codevil.tracne.ui.home2.components

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import tech.codevil.tracne.model.Template
import tech.codevil.tracne.ui.statistics.MultipleGraphView

@Parcelize
data class TemplateGraph(
    val template: Template, val graph: MultipleGraphView.Graph,
    val startTimestamp: Long, val endTimestamp: Long,
) : Parcelable