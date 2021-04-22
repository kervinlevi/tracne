package tech.codevil.tracne.ui.home2.components

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import tech.codevil.tracne.ui.statistics.MultipleGraphView

@Parcelize
data class ParameterItem(
    val id: String, val label: String, val graph: MultipleGraphView.Graph,
    val startTimestamp: Long, val endTimestamp: Long,
) : Parcelable