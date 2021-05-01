package tech.codevil.tracne.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.Date

/**
 * Created by kervin.decena on 21/03/2021.
 */
@Parcelize
data class Entry(
    var timestamp: Long,
    var day: Date,
    var values: Map<String, Int>,
    var lastUpdated: Long
) : Parcelable